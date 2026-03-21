import json
with open(r'C:\Program Files (x86)\Fractal Softworks\Starsector\mods\SecondInCommand\frontend\api.json', 'r', encoding='utf-8') as f:
    data = json.load(f)
for apt in data['aptitudes']:
    cats = apt.get('categories', [])
    if cats:
        print("Aptitude:", apt['id'])
        print("Categories:", json.dumps(cats, indent=2))
        break

