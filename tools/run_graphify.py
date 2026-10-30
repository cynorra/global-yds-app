import sys
import json
from pathlib import Path
from graphify.detect import detect, save_manifest
from graphify.extract import collect_files, extract
from graphify.build import build_from_json
from graphify.cluster import cluster, score_all
from graphify.analyze import god_nodes, surprising_connections, suggest_questions
from graphify.report import generate
from graphify.export import to_json, to_html

out_dir = Path('graphify-out')
out_dir.mkdir(exist_ok=True)
(out_dir / '.graphify_python').write_text(sys.executable, encoding='utf-8')
(out_dir / '.graphify_root').write_text(str(Path('.').resolve()), encoding='utf-8')

print("1. Dosyalar taranıyor...")
det = detect(Path('.'))
print(f"Toplam dosya: {det.get('total_files', 0)}")

print("2. AST çıkarımı yapılıyor...")
code_files = []
for f in det.get('files', {}).get('code', []):
    p = Path(f)
    code_files.extend(collect_files(p) if p.is_dir() else [p])

print(f"{len(code_files)} kod dosyası analiz ediliyor...")
ast_res = extract(code_files, cache_root=Path('.'))
print(f"AST tamamlandı: {len(ast_res['nodes'])} düğüm, {len(ast_res['edges'])} bağlantı")

merged = {
    'nodes': ast_res['nodes'],
    'edges': ast_res['edges'],
    'hyperedges': [],
    'input_tokens': 0,
    'output_tokens': 0
}

print("3. Graf oluşturuluyor ve kümeleniyor...")
G = build_from_json(merged, root='.', directed=False)
communities = cluster(G)
cohesion = score_all(G, communities)
gods = god_nodes(G)
surprises = surprising_connections(G, communities)
labels = {cid: f"Community {cid}" for cid in communities}
questions = suggest_questions(G, communities, labels)

print("4. JSON ve Rapor dosyaları kaydediliyor...")
to_json(G, communities, str(out_dir / 'graph.json'), force=True)
report = generate(G, communities, cohesion, labels, gods, surprises, det, {'input': 0, 'output': 0}, '.', suggested_questions=questions)
(out_dir / 'GRAPH_REPORT.md').write_text(report, encoding='utf-8')

print("5. HTML görselleştirme üretiliyor...")
try:
    to_html(G, communities, str(out_dir / 'graph.html'), community_labels=labels)
except Exception as e:
    print(f"HTML export uyarısı: {e}")

try:
    save_manifest(det.get('all_files') or det['files'], root='.')
except Exception as e:
    pass

print(f"Bitti! Graf: {G.number_of_nodes()} düğüm, {G.number_of_edges()} bağlantı, {len(communities)} topluluk.")
