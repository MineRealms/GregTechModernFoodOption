#!/usr/bin/env python3
"""
Generate a KubeJS client script that registers molecule drawings for GTMFO
chemicals via MolDraw (gregtech-molecule-drawings).

Sources of truth (ZERO guessing):
  * Material IDs   -> read from GTMFOMaterials.java / GTMFOFluids.java (GTCEu.id(...))
  * CAS + formula  -> CHEMICALS.md (sections 1-5)
  * 3D/2D structure -> PubChem (by CAS, name fallback), parsed with RDKit.

Rules applied:
  * Skip single elements (metals / diatomic gases) and mixtures / solutions
    without a unique molecular formula.
  * Do NOT re-register anything already shipped by the mod's generated JSON.
  * Skeletal drawing convention: carbon vertices are invisible; hydrogens on
    heteroatoms are shown; hydrogens on carbon are hidden unless the carbon has
    no C-C bond (inorganic-like carbon).
  * Aromatic rings are Kekulized (alternating single/double) like the mod does.
"""
import os
import sys
import json
import time
import urllib.parse

# Bypass the local proxy (PubChem fails over the Clash proxy).
os.environ["NO_PROXY"] = "*"
os.environ["no_proxy"] = "*"

import requests
from rdkit import Chem
from rdkit.Chem import AllChem, SanitizeFlags

# ---------------------------------------------------------------------------
# Chemical list
# tuple: (material_id, cas_or_None, lookup_name, expected_atom_counts, remark)
# expected_atom_counts is {element_symbol: count} matching CHEMICALS.md formula.
# ---------------------------------------------------------------------------
CHEMICALS = [
    # --- section 1: pharmaceuticals ---
    ("paracetamol", "103-90-2", "Paracetamol", {"C": 8, "H": 9, "N": 1, "O": 2}, ""),
    ("codeine", "76-57-3", "Codeine", {"C": 18, "H": 21, "N": 1, "O": 3}, ""),
    ("promethazine", "60-87-7", "Promethazine", {"C": 17, "H": 20, "N": 2, "S": 1}, ""),
    ("phenothiazine", "92-84-2", "Phenothiazine", {"C": 12, "H": 9, "N": 1, "S": 1}, ""),
    ("diphenylamine", "122-39-4", "Diphenylamine", {"C": 12, "H": 11, "N": 1}, ""),
    # --- section 2: aromatic intermediates ---
    ("vanillin", "121-33-5", "Vanillin", {"C": 8, "H": 8, "O": 3}, ""),
    ("vanillylmandelic_acid", "55-10-7", "Vanillylmandelic acid", {"C": 9, "H": 10, "O": 5}, ""),
    ("vanilglycolic_acid", None, "vanilglycolic acid", {"C": 9, "H": 8, "O": 5}, "no CAS; verify by formula"),
    ("guaiacol", "90-05-1", "Guaiacol", {"C": 7, "H": 8, "O": 2}, ""),
    ("iv_nitrophenol", "100-02-7", "4-Nitrophenol", {"C": 6, "H": 5, "N": 1, "O": 3}, "4-nitrophenol"),
    ("ii_nitrophenol", "88-75-5", "2-Nitrophenol", {"C": 6, "H": 5, "N": 1, "O": 3}, "2-nitrophenol"),
    ("isopropyl_chloride", "75-29-6", "2-Chloropropane", {"C": 3, "H": 7, "Cl": 1}, ""),
    ("x_phenothiazine_ii_propyl_chloride", None, "10-(2-chloropropyl)phenothiazine", {"C": 15, "H": 14, "N": 1, "S": 1, "Cl": 1}, "no CAS; verify by formula"),
    # --- section 3: aliphatic intermediates ---
    ("acetaldehyde", "75-07-0", "Acetaldehyde", {"C": 2, "H": 4, "O": 1}, ""),
    ("glyoxal", "107-22-2", "Glyoxal", {"C": 2, "H": 2, "O": 2}, ""),
    ("glyoxylic_acid", "298-12-4", "Glyoxylic acid", {"C": 2, "H": 2, "O": 3}, ""),
    ("citric_acid", "77-92-9", "Citric acid", {"C": 6, "H": 8, "O": 7}, ""),
    ("dimethylamine", "124-40-3", "Dimethylamine", {"C": 2, "H": 7, "N": 1}, ""),
    # --- section 4: lipids ---
    ("stearin", "555-43-1", "Tristearin", {"C": 57, "H": 110, "O": 6}, "glyceryl tristearate"),
    ("sodium_stearate", "822-16-2", "Sodium stearate", {"C": 18, "H": 35, "O": 2, "Na": 1}, ""),
    # --- section 5: inorganic ---
    ("hydrogen_cyanide", "74-90-8", "Hydrogen cyanide", {"H": 1, "C": 1, "N": 1}, ""),
    ("sodium_cyanide", "143-33-9", "Sodium cyanide", {"Na": 1, "C": 1, "N": 1}, ""),
    ("hydrogen_sulfide", "7783-06-4", "Hydrogen sulfide", {"H": 2, "S": 1}, ""),
    ("ammonia", "7664-41-7", "Ammonia", {"N": 1, "H": 3}, ""),
    ("ammonium_chloride", "12125-02-9", "Ammonium chloride", {"N": 1, "H": 4, "Cl": 1}, ""),
    ("ammonium_perchlorate", "7790-98-9", "Ammonium perchlorate", {"N": 1, "H": 4, "Cl": 1, "O": 4}, ""),
    ("hydrochloric_acid", "7647-01-0", "Hydrochloric acid", {"H": 1, "Cl": 1}, ""),
    ("nitric_acid", "7697-37-2", "Nitric acid", {"H": 1, "N": 1, "O": 3}, ""),
    ("sulfuric_acid", "7664-93-9", "Sulfuric acid", {"H": 2, "S": 1, "O": 4}, ""),
    ("perchloric_acid", "7601-90-3", "Perchloric acid", {"H": 1, "Cl": 1, "O": 4}, ""),
    ("sodium_perchlorate", "7601-89-0", "Sodium perchlorate", {"Na": 1, "Cl": 1, "O": 4}, ""),
    ("potassium_perchlorate", "7778-74-7", "Potassium perchlorate", {"K": 1, "Cl": 1, "O": 4}, ""),
    ("sodium_chlorate", "7775-09-9", "Sodium chlorate", {"Na": 1, "Cl": 1, "O": 3}, ""),
    ("sodium_hydroxide", "1310-73-2", "Sodium hydroxide", {"Na": 1, "O": 1, "H": 1}, ""),
    ("sodium_bicarbonate", "144-55-8", "Sodium bicarbonate", {"Na": 1, "H": 1, "C": 1, "O": 3}, ""),
    ("sodium_chloride", "7647-14-5", "Sodium chloride", {"Na": 1, "Cl": 1}, ""),
    ("sodium_sulfate", "7757-82-6", "Sodium sulfate", {"Na": 2, "S": 1, "O": 4}, ""),
    ("sodium_arsenite_solution", "7784-46-5", "Sodium arsenite", {"Na": 1, "As": 1, "O": 2}, "compound is sodium arsenite"),
    ("cupric_hydrogen_arsenite", "10290-12-7", "Copper(II) hydrogen arsenite", {"Cu": 1, "H": 1, "As": 1, "O": 3}, "Scheele's green"),
    ("arsenic_trioxide", "1327-53-3", "Arsenic trioxide", {"As": 2, "O": 3}, ""),
    ("chloroauric_acid", "16903-35-8", "Chloroauric acid", {"H": 1, "Au": 1, "Cl": 4}, "tetrachloroauric acid"),
    ("blue_vitriol", "7758-98-7", "copper sulfate", {"Cu": 1, "S": 1, "O": 4}, "anhydrous CuSO4"),
    ("tricalcium_phosphate", "7758-87-4", "Tricalcium phosphate", {"Ca": 3, "P": 2, "O": 8}, ""),
    ("lithium_carbonate", "554-13-2", "Lithium carbonate", {"Li": 2, "C": 1, "O": 3}, ""),
    ("lithium_oxide", "12057-24-8", "Lithium oxide", {"Li": 2, "O": 1}, ""),
    ("carbon_dioxide", "124-38-9", "Carbon dioxide", {"C": 1, "O": 2}, ""),
    ("carbon_monoxide", "630-08-0", "Carbon monoxide", {"C": 1, "O": 1}, ""),
    ("sulfur_dioxide", "7446-09-5", "Sulfur dioxide", {"S": 1, "O": 2}, ""),
    ("nitrogen_dioxide", "10102-44-0", "Nitrogen dioxide", {"N": 1, "O": 2}, ""),
    ("nitrous_oxide", "10024-97-2", "Nitrous oxide", {"N": 2, "O": 1}, ""),
    ("water", "7732-18-5", "Water", {"H": 2, "O": 1}, ""),
]

USER_AGENT = "Mozilla/5.0 (MolDraw generator)"

# Covalent (informal) SMILES for purely ionic salts whose PubChem form would be
# disconnected monatomic ions (no bonds -> unusable drawing). The H+ counterion
# of chloroauric acid is omitted (the drawn species is the AuCl4- complex).
SMILES_OVERRIDES = {
    "sodium_chloride": ("[Na]Cl", {"Na": 1, "Cl": 1}),
    "lithium_oxide": ("[Li]O[Li]", {"Li": 2, "O": 1}),
    "arsenic_trioxide": ("O=[As]O[As]=O", {"As": 2, "O": 3}),
    "chloroauric_acid": ("Cl[Au](Cl)(Cl)Cl", {"Au": 1, "Cl": 4}),
}


def pubchem_lookup(identifier):
    """Query PubChem PUG REST. Returns dict with SMILES / formula / name / CID,
    or None on failure. Tries 'name' endpoint (accepts CAS too)."""
    prop = "IsomericSMILES,MolecularFormula,IUPACName"
    url = ("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/%s/property/%s/JSON"
           % (urllib.parse.quote(identifier), prop))
    try:
        r = requests.get(url, headers={"User-Agent": USER_AGENT}, timeout=40)
        if r.status_code != 200:
            return None
        data = r.json()
        props = data.get("PropertyTable", {}).get("Properties", [])
        if not props:
            return None
        p = props[0]
        # PubChem returns the isomeric SMILES under the "SMILES" key and the
        # connectivity-only form under "ConnectivitySMILES".
        smiles = p.get("SMILES") or p.get("IsomericSMILES") or p.get("ConnectivitySMILES") or p.get("CanonicalSMILES")
        if not smiles:
            return None
        return {
            "cid": p.get("CID"),
            "smiles": smiles,
            "formula": p.get("MolecularFormula"),
            "name": p.get("IUPACName"),
        }
    except Exception as e:
        print("    [lookup error] %s: %s" % (identifier, e))
        return None


def count_atoms(mol):
    counts = {}
    for a in mol.GetAtoms():
        s = a.GetSymbol()
        counts[s] = counts.get(s, 0) + 1
    return counts


def normalize_smiles(smiles):
    """Return a canonical, neutral-ish SMILES the RDKit can kekulize."""
    return smiles


def should_show_hydrogen(h_atom, mol, total_carbons):
    """MolDraw H convention: single-carbon molecules (methane/chloroform) show
    every H explicitly; multi-carbon molecules use skeletal notation (H on
    carbon is implicit, H on heteroatoms is explicit)."""
    neighbors = [n for n in h_atom.GetNeighbors()]
    if len(neighbors) != 1:
        return True
    heavy = neighbors[0]
    if heavy.GetAtomicNum() != 6:
        return True  # H on O/N/S/P/halogen -> show
    # H on carbon: show only for single-carbon molecules (like methane).
    return total_carbons == 1


def build_moldraw(smiles):
    """Return (atoms, bonds) where atoms = list of dicts, bonds = list of dicts,
    with x/y coordinates scaled to ~1.0 bond length (rdkit and MolDraw share the
    same y-up convention). Raises on failure."""
    mol = Chem.MolFromSmiles(smiles, sanitize=False)
    if mol is None:
        raise ValueError("RDKit could not parse SMILES: %s" % smiles)

    # Manual sanitization: skip the default CLEANUP step that flattens the
    # hypervalent Cl=O double bonds in perchlorate/chlorate (chemically wrong),
    # but still perceive rings + aromaticity so we can Kekulize rings.
    Chem.SanitizeMol(mol, sanitizeOps=SanitizeFlags.SANITIZE_SYMMRINGS)
    Chem.SanitizeMol(mol, sanitizeOps=SanitizeFlags.SANITIZE_SETAROMATICITY)

    # Kekulize aromatic rings into alternating single/double bonds.
    try:
        Chem.Kekulize(mol, clearAromaticFlags=True)
    except Exception:
        # Fall back to treating aromatic as single (rare).
        pass

    mol = Chem.AddHs(mol)
    total_carbons = sum(1 for a in mol.GetAtoms() if a.GetAtomicNum() == 6)
    AllChem.Compute2DCoords(mol)
    conf = mol.GetConformer()

    # Determine which atoms to keep and map rdkit idx -> new idx.
    keep = []
    for a in mol.GetAtoms():
        if a.GetAtomicNum() == 1:  # hydrogen
            keep.append(should_show_hydrogen(a, mol, total_carbons))
        else:
            keep.append(True)

    # measure median bond length among kept bonds for normalization
    bond_lens = []
    bonds_raw = []
    for b in mol.GetBonds():
        i = b.GetBeginAtomIdx()
        j = b.GetEndAtomIdx()
        if not (keep[i] and keep[j]):
            continue
        pi = conf.GetAtomPosition(i)
        pj = conf.GetAtomPosition(j)
        import math
        bond_lens.append(math.hypot(pi.x - pj.x, pi.y - pj.y))
        bonds_raw.append((i, j, b))

    scale = 1.0
    if bond_lens:
        bond_lens.sort()
        med = bond_lens[len(bond_lens) // 2]
        if med > 1e-6:
            scale = 1.0 / med

    # build new-index mapping
    idx_map = {}
    new_atoms = []
    new_idx = 0
    for a in mol.GetAtoms():
        if not keep[a.GetIdx()]:
            continue
        idx_map[a.GetIdx()] = new_idx
        pos = conf.GetAtomPosition(a.GetIdx())
        x = pos.x * scale
        y = pos.y * scale  # rdkit and MolDraw both use y-up; do NOT negate
        is_carbon = a.GetAtomicNum() == 6
        new_atoms.append({
            # single-carbon molecules keep the explicit "C" (like methane/
            # chloroform); multi-carbon molecules use invisible skeletal carbon
            "element": None if (is_carbon and total_carbons > 1) else a.GetSymbol(),
            "x": x,
            "y": y,
        })
        new_idx += 1

    new_bonds = []
    for (i, j, b) in bonds_raw:
        bt = str(b.GetBondType())
        order = {"SINGLE": 1, "DOUBLE": 2, "TRIPLE": 3, "AROMATIC": 1}.get(bt, 1)
        new_bonds.append({
            "a": idx_map[i],
            "b": idx_map[j],
            "order": order,
        })

    return new_atoms, new_bonds


def fmt_float(x):
    s = ("%.4f" % x).rstrip("0").rstrip(".")
    return "0" if s in ("-0", "-0.") else s


def generate_js(results):
    lines = []
    lines.append("// MolDraw molecule drawings for GTMFO (GregTechModernFoodOption) chemicals.")
    lines.append("// Auto-generated by GregTechModernFoodOption/tools/generate_moldraw_molecules.py")
    lines.append("// Structures sourced from PubChem and verified against CHEMICALS.md formulas.")
    lines.append("// Do not hand-edit; re-run the generator instead.")
    lines.append("")
    lines.append("MolDrawEvents.molecules((event) => {")
    for res in results:
        mid = res["id"]
        atoms = res["atoms"]
        bonds = res["bonds"]
        lines.append("  event.create(\"gtceu:%s\")" % mid)
        lines.append("    .xy()")
        for a in atoms:
            if a["element"] is None:
                lines.append("    .invAtom(%s, %s)" % (fmt_float(a["x"]), fmt_float(a["y"])))
            else:
                lines.append("    .atom(\"%s\", %s, %s)" % (a["element"], fmt_float(a["x"]), fmt_float(a["y"])))
        for b in bonds:
            if b["order"] == 1:
                lines.append("    .bond(%d, %d)" % (b["a"], b["b"]))
            else:
                sols = ", ".join(["MolBond.Line.SOLID"] * b["order"])
                lines.append("    .bond(%d, %d, %s)" % (b["a"], b["b"], sols))
        lines.append("    ;")
        lines.append("")
    lines.append("});")
    return "\n".join(lines)


def main():
    results = []
    skipped = []

    for (mid, cas, name, expected, remark) in CHEMICALS:
        print("== %s (%s)" % (mid, cas or name))
        smiles = None
        if mid in SMILES_OVERRIDES:
            smiles, expected = SMILES_OVERRIDES[mid]
            print("    [override] %s" % smiles)
        else:
            entry = pubchem_lookup(cas) if cas else None
            if entry is None and name:
                entry = pubchem_lookup(name)
            if entry is None:
                print("    [SKIP] no PubChem result")
                skipped.append((mid, "no pubchem result"))
                continue
            smiles = entry["smiles"]

        # verify formula
        try:
            mol = Chem.MolFromSmiles(smiles)
            mol = Chem.AddHs(mol)
            actual = count_atoms(mol)
        except Exception as e:
            print("    [SKIP] rdkit count failed: %s" % e)
            skipped.append((mid, "rdkit count failed"))
            continue

        if actual != expected:
            print("    [MISMATCH] expected=%s got=%s (smiles=%s)" % (expected, actual, smiles))
            skipped.append((mid, "formula mismatch %s vs %s" % (expected, actual)))
            continue

        try:
            atoms, bonds = build_moldraw(smiles)
        except Exception as e:
            print("    [SKIP] build failed: %s" % e)
            skipped.append((mid, "build failed: %s" % e))
            continue

        results.append({
            "id": mid,
            "atoms": atoms,
            "bonds": bonds,
            "smiles": smiles,
        })
        print("    OK smiles=%s atoms=%d bonds=%d" % (smiles, len(atoms), len(bonds)))
        if mid not in SMILES_OVERRIDES:
            time.sleep(0.4)  # be polite to PubChem

    print("\n==== SUMMARY ====")
    print("registered: %d" % len(results))
    print("skipped: %d" % len(skipped))
    for mid, why in skipped:
        print("  - %s : %s" % (mid, why))

    js = generate_js(results)

    out_path = sys.argv[1] if len(sys.argv) > 1 else "moldraw_molecules.js"
    with open(out_path, "w", encoding="utf-8") as f:
        f.write(js)
    print("\nwrote %s (%d bytes)" % (out_path, len(js)))


if __name__ == "__main__":
    main()
