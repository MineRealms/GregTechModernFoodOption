#!/usr/bin/env python3
"""
Batch-generate MolDraw molecule JSON files for GTMFO chemicals.

Pipeline (zero guessing for connectivity):
  * Material IDs / CAS / formula  -> from CHEMICALS.md + GTMFO source
  * SMILES                        -> PubChem (by CAS, name fallback)
  * 2D coordinates + bond orders  -> RDKit (Compute2DCoords), so 120-deg
    trigonal-planar geometry (sp2 amide N, carbonyl C) is automatic.
  * Aromatic 6-rings              -> mapped to MolDraw "benzene" component
    (non-fused rings only; fused rings fall back to Kekulized single/double
    bonds, exactly like the mod's own naphthalene).
  * Carbon convention             -> invisible skeletal carbon for multi-carbon
    molecules; explicit "C" for single-carbon molecules (methane/chloroform).
  * Hydrogens                     -> shown on heteroatoms; on carbon only for
    single-carbon molecules (skeletal otherwise).

Output: one JSON file per material under assets/gtceu/molecules/<id>.json
"""
import os
import sys
import json
import math
import time
import urllib.parse

os.environ["NO_PROXY"] = "*"
os.environ["no_proxy"] = "*"

import requests
from rdkit import Chem
from rdkit.Chem import AllChem, SanitizeFlags

# (material_id, cas_or_None, lookup_name, expected_atom_counts, remark)
CHEMICALS = [
    ("paracetamol", "103-90-2", "Paracetamol", {"C": 8, "H": 9, "N": 1, "O": 2}, ""),
    ("codeine", "76-57-3", "Codeine", {"C": 18, "H": 21, "N": 1, "O": 3}, ""),
    ("promethazine", "60-87-7", "Promethazine", {"C": 17, "H": 20, "N": 2, "S": 1}, ""),
    ("phenothiazine", "92-84-2", "Phenothiazine", {"C": 12, "H": 9, "N": 1, "S": 1}, ""),
    ("diphenylamine", "122-39-4", "Diphenylamine", {"C": 12, "H": 11, "N": 1}, ""),
    ("vanillin", "121-33-5", "Vanillin", {"C": 8, "H": 8, "O": 3}, ""),
    ("vanillylmandelic_acid", "55-10-7", "Vanillylmandelic acid", {"C": 9, "H": 10, "O": 5}, ""),
    ("vanilglycolic_acid", None, "vanilglycolic acid", {"C": 9, "H": 8, "O": 5}, "no CAS"),
    ("guaiacol", "90-05-1", "Guaiacol", {"C": 7, "H": 8, "O": 2}, ""),
    ("iv_nitrophenol", "100-02-7", "4-Nitrophenol", {"C": 6, "H": 5, "N": 1, "O": 3}, ""),
    ("ii_nitrophenol", "88-75-5", "2-Nitrophenol", {"C": 6, "H": 5, "N": 1, "O": 3}, ""),
    ("isopropyl_chloride", "75-29-6", "2-Chloropropane", {"C": 3, "H": 7, "Cl": 1}, ""),
    ("x_phenothiazine_ii_propyl_chloride", None, "10-(2-chloropropyl)phenothiazine",
     {"C": 15, "H": 14, "N": 1, "S": 1, "Cl": 1}, "no CAS"),
    ("acetaldehyde", "75-07-0", "Acetaldehyde", {"C": 2, "H": 4, "O": 1}, ""),
    ("glyoxal", "107-22-2", "Glyoxal", {"C": 2, "H": 2, "O": 2}, ""),
    ("glyoxylic_acid", "298-12-4", "Glyoxylic acid", {"C": 2, "H": 2, "O": 3}, ""),
    ("citric_acid", "77-92-9", "Citric acid", {"C": 6, "H": 8, "O": 7}, ""),
    ("dimethylamine", "124-40-3", "Dimethylamine", {"C": 2, "H": 7, "N": 1}, ""),
    ("stearin", "555-43-1", "Tristearin", {"C": 57, "H": 110, "O": 6}, "glyceryl tristearate"),
    ("sodium_stearate", "822-16-2", "Sodium stearate", {"C": 18, "H": 35, "O": 2, "Na": 1}, ""),
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
    ("sodium_arsenite_solution", "7784-46-5", "Sodium arsenite", {"Na": 1, "As": 1, "O": 2}, ""),
    ("cupric_hydrogen_arsenite", "10290-12-7", "Copper(II) hydrogen arsenite", {"Cu": 1, "H": 1, "As": 1, "O": 3}, ""),
    ("arsenic_trioxide", "1327-53-3", "Arsenic trioxide", {"As": 2, "O": 3}, ""),
    ("chloroauric_acid", "16903-35-8", "Chloroauric acid", {"H": 1, "Au": 1, "Cl": 4}, ""),
    ("blue_vitriol", "7758-98-7", "copper sulfate", {"Cu": 1, "S": 1, "O": 4}, "anhydrous"),
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

# Covalent SMILES for purely ionic salts whose PubChem form is disconnected.
SMILES_OVERRIDES = {
    "sodium_chloride": ("[Na]Cl", {"Na": 1, "Cl": 1}),
    "lithium_oxide": ("[Li]O[Li]", {"Li": 2, "O": 1}),
    "arsenic_trioxide": ("O=[As]O[As]=O", {"As": 2, "O": 3}),
    "chloroauric_acid": ("Cl[Au](Cl)(Cl)Cl", {"Au": 1, "Cl": 4}),
}

USER_AGENT = "Mozilla/5.0 (MolDraw generator)"


def pubchem_lookup(identifier):
    prop = "IsomericSMILES,MolecularFormula,IUPACName"
    url = ("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/name/%s/property/%s/JSON"
           % (urllib.parse.quote(identifier), prop))
    try:
        r = requests.get(url, headers={"User-Agent": USER_AGENT}, timeout=40)
        if r.status_code != 200:
            return None
        props = r.json().get("PropertyTable", {}).get("Properties", [])
        if not props:
            return None
        p = props[0]
        smiles = p.get("SMILES") or p.get("IsomericSMILES") or p.get("ConnectivitySMILES") or p.get("CanonicalSMILES")
        if not smiles:
            return None
        return {"smiles": smiles, "formula": p.get("MolecularFormula"), "name": p.get("IUPACName")}
    except Exception:
        return None


def count_atoms(mol):
    counts = {}
    for a in mol.GetAtoms():
        s = a.GetSymbol()
        counts[s] = counts.get(s, 0) + 1
    return counts


def detect_simple_aromatic_rings(mol):
    """Return a list of 6-membered aromatic rings whose atoms are NOT shared
    with another aromatic ring (i.e. fused rings are excluded, matching the
    mod's own convention where naphthalene uses Kekule bonds)."""
    ri = mol.GetRingInfo()
    rings = []
    for ring in ri.AtomRings():
        if len(ring) != 6:
            continue
        if all(mol.GetAtomWithIdx(i).GetIsAromatic() for i in ring):
            rings.append(tuple(ring))
    # count aromatic-ring membership per atom
    membership = {}
    for ring in rings:
        for i in ring:
            membership[i] = membership.get(i, 0) + 1
    return [r for r in rings if all(membership[i] == 1 for i in r)]


def should_show_hydrogen(h_atom, total_carbons):
    neighbors = [n for n in h_atom.GetNeighbors()]
    if len(neighbors) != 1:
        return True
    heavy = neighbors[0]
    if heavy.GetAtomicNum() != 6:
        return True
    return total_carbons == 1


def build_molecule(smiles):
    """Return the MolDraw 'contents' list (atoms/bonds/benzene components)."""
    mol = Chem.MolFromSmiles(smiles, sanitize=False)
    if mol is None:
        raise ValueError("RDKit could not parse SMILES: %s" % smiles)

    # Manual sanitize: skip CLEANUP (flattens Cl=O in perchlorate), but
    # perceive rings + aromaticity so we can Kekulize.
    Chem.SanitizeMol(mol, sanitizeOps=SanitizeFlags.SANITIZE_SYMMRINGS)
    Chem.SanitizeMol(mol, sanitizeOps=SanitizeFlags.SANITIZE_SETAROMATICITY)

    simple_rings = detect_simple_aromatic_rings(mol)

    try:
        Chem.Kekulize(mol, clearAromaticFlags=True)
    except Exception:
        pass

    mol = Chem.AddHs(mol)
    total_carbons = sum(1 for a in mol.GetAtoms() if a.GetAtomicNum() == 6)
    AllChem.Compute2DCoords(mol)
    conf = mol.GetConformer()

    n_atoms = mol.GetNumAtoms()
    # which atoms to keep (H rule)
    keep = []
    for a in mol.GetAtoms():
        if a.GetAtomicNum() == 1:
            keep.append(should_show_hydrogen(a, total_carbons))
        else:
            keep.append(True)

    # ---- 1. compute coordinates for kept atoms ----
    pts = []
    kept_indices = []
    for a in mol.GetAtoms():
        i = a.GetIdx()
        if not keep[i]:
            continue
        p = conf.GetAtomPosition(i)
        pts.append((p.x, p.y))
        kept_indices.append(i)

# ---- 2. rotate to align first benzene ring edge to nearest 45° multiple ----
    rot_delta = 0.0
    if simple_rings:
        r = list(simple_rings[0])
        p0 = conf.GetAtomPosition(r[0])
        p1 = conf.GetAtomPosition(r[1])
        v1 = (p1.x - p0.x, p1.y - p0.y)
        edge_ang = math.atan2(v1[1], v1[0])
        snap_ang = round(math.degrees(edge_ang) / 45) * (math.pi / 4)
        delta = snap_ang - edge_ang
        if abs(delta) > 1e-6:
            cos_d = math.cos(delta)
            sin_d = math.sin(delta)
            # compute rotation center from kept heavy atoms
            heavy_pts = []
            for idx, i in enumerate(kept_indices):
                a = mol.GetAtomWithIdx(i)
                if a.GetAtomicNum() != 1:
                    p = conf.GetAtomPosition(i)
                    heavy_pts.append((p.x, p.y))
            if heavy_pts:
                cx = sum(p[0] for p in heavy_pts) / len(heavy_pts)
                cy = sum(p[1] for p in heavy_pts) / len(heavy_pts)
            else:
                cx = cy = 0.0
            cos_d = math.cos(delta)
            sin_d = math.sin(delta)
            rotated_pts = []
            for (x, y) in pts:
                xc = x - cx
                yc = y - cy
                xr = xc * cos_d - yc * sin_d
                yr = xc * sin_d + yc * cos_d
                rotated_pts.append((xr + cx, yr + cy))
            pts = rotated_pts
            rot_delta = delta
        else:
            rot_delta = 0.0
    else:
        rot_delta = 0.0

    # median bond length among kept heavy-heavy bonds, for scaling to ~1.0
    bond_lens = []
    for b in mol.GetBonds():
        i, j = b.GetBeginAtomIdx(), b.GetEndAtomIdx()
        if not (keep[i] and keep[j]):
            continue
        ai, aj = mol.GetAtomWithIdx(i), mol.GetAtomWithIdx(j)
        if ai.GetAtomicNum() == 1 or aj.GetAtomicNum() == 1:
            continue
        pi, pj = conf.GetAtomPosition(i), conf.GetAtomPosition(j)
        bond_lens.append(math.hypot(pi.x - pj.x, pi.y - pj.y))
    scale = 1.0
    if bond_lens:
        bond_lens.sort()
        med = bond_lens[len(bond_lens) // 2]
        if med > 1e-6:
            scale = 1.0 / med

    def xy(idx):
        # find index in kept_indices
        try:
            pos = kept_indices.index(idx)
            x, y = pts[pos]
        except ValueError:
            p = conf.GetAtomPosition(idx)
            return (p.x * scale, p.y * scale)
        return (x * scale, y * scale)

    # map rdkit index -> new (mod) index; ring atoms handled by benzene comp
    ring_atom_to_new = {}   # rdkit idx -> (ring_id, position_in_ring)
    ring_specs = []         # (first_rdkit_idx, next_rdkit_idx, angle, list_of_6_rdkit_idx)

    next_index = 0
    # assign ring indices first (consecutive per ring)
    for ring in simple_rings:
        r = list(ring)
        # detect direction via cross product of first two edges
        p0 = conf.GetAtomPosition(r[0])
        p1 = conf.GetAtomPosition(r[1])
        p2 = conf.GetAtomPosition(r[2])
        v1 = (p1.x - p0.x, p1.y - p0.y)
        v2 = (p2.x - p1.x, p2.y - p1.y)
        cross = v1[0] * v2[1] - v1[1] * v2[0]
        angle = 0.0 if cross > 0 else math.pi
        for i in r:
            ring_atom_to_new[i] = (len(ring_specs), None)
        ring_specs.append((r[0], r[1], angle, r))
        for i in r:
            ring_atom_to_new[i] = (len(ring_specs) - 1, None)

    # assign new indices: rings first, then other kept atoms
    new_index_of = {}  # rdkit idx -> new idx
    # ring atoms: consecutive indices
    for ring_id, (_, _, _, r) in enumerate(ring_specs):
        for pos, rd in enumerate(r):
            new_index_of[rd] = ring_id * 6 + pos
    next_free = len(ring_specs) * 6
    for a in mol.GetAtoms():
        i = a.GetIdx()
        if i in new_index_of:
            continue
        if not keep[i]:
            continue
        new_index_of[i] = next_free
        next_free += 1

    contents = []

    # benzene components
    for ring_id, (first_rd, next_rd, angle, r) in enumerate(ring_specs):
        fx, fy = xy(first_rd)
        nx, ny = xy(next_rd)
        indices = [new_index_of[rd] for rd in r]
        contents.append({
            "indices": indices,
            "x0": round(fx, 7),
            "y0": round(fy, 7),
            "x1": round(nx, 7),
            "y1": round(ny, 7),
            "angle": round(angle + rot_delta, 7),
            "type": "benzene",
        })

    # individual atoms (non-ring kept atoms)
    for a in mol.GetAtoms():
        i = a.GetIdx()
        if not keep[i] or i in ring_atom_to_new:
            continue
        x, y = xy(i)
        is_carbon = a.GetAtomicNum() == 6
        element = None if (is_carbon and total_carbons > 1) else a.GetSymbol()
        obj = {"index": new_index_of[i], "x": round(x, 7), "y": round(y, 7), "type": "atom"}
        if element is not None:
            obj["element"] = element
        contents.append(obj)

    # bonds: skip ring-internal bonds (handled by benzene comp)
    ring_internal = set()
    for _, _, _, r in ring_specs:
        for k in range(6):
            ring_internal.add((r[k], r[(k + 1) % 6]))
    for b in mol.GetBonds():
        i, j = b.GetBeginAtomIdx(), b.GetEndAtomIdx()
        if not (keep[i] and keep[j]):
            continue
        if (i, j) in ring_internal or (j, i) in ring_internal:
            continue
        bt = str(b.GetBondType())
        order = {"SINGLE": 1, "DOUBLE": 2, "TRIPLE": 3, "AROMATIC": 1}.get(bt, 1)
        contents.append({
            "a": new_index_of[i],
            "b": new_index_of[j],
            "lines": ["solid"] * order,
            "type": "bond",
        })

    return contents


def main():
    out_dir = sys.argv[1] if len(sys.argv) > 1 else "molecules"
    os.makedirs(out_dir, exist_ok=True)

    ok, skipped = [], []
    for (mid, cas, name, expected, remark) in CHEMICALS:
        print("== %s" % mid)
        smiles = None
        if mid in SMILES_OVERRIDES:
            smiles, expected = SMILES_OVERRIDES[mid]
        else:
            entry = pubchem_lookup(cas) if cas else None
            if entry is None and name:
                entry = pubchem_lookup(name)
            if entry is None:
                print("  [SKIP] no PubChem result")
                skipped.append((mid, "no pubchem result"))
                continue
            smiles = entry["smiles"]

        # verify formula
        m = Chem.MolFromSmiles(smiles)
        if m is None:
            skipped.append((mid, "bad smiles"))
            continue
        actual = count_atoms(Chem.AddHs(m))
        if actual != expected:
            print("  [MISMATCH] expected=%s got=%s (%s)" % (expected, actual, smiles))
            skipped.append((mid, "formula mismatch"))
            continue

        try:
            contents = build_molecule(smiles)
        except Exception as e:
            print("  [SKIP] build failed: %s" % e)
            skipped.append((mid, "build failed"))
            continue

        path = os.path.join(out_dir, mid + ".json")
        with open(path, "w", encoding="utf-8") as f:
            json.dump({"contents": contents}, f, indent=2)
        ok.append(mid)
        print("  OK  %s (%d elements)" % (smiles, len(contents)))
        if mid not in SMILES_OVERRIDES:
            time.sleep(0.4)

    print("\n==== SUMMARY ====")
    print("written: %d" % len(ok))
    print("skipped: %d" % len(skipped))
    for mid, why in skipped:
        print("  - %s : %s" % (mid, why))


if __name__ == "__main__":
    main()
