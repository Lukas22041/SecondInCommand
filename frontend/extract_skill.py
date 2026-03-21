import json
with open(r'C:\Program Files (x86)\Fractal Softworks\Starsector\mods\SecondInCommand\frontend\api.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

# Find skills with imageWithText in tooltipElements
def find_image_with_text(obj, path='', found=[]):
    if isinstance(obj, dict):
        if obj.get('type') == 'imageWithText':
            found.append((path, obj))
            return
        for k, v in obj.items():
            find_image_with_text(v, path + '.' + k, found)
    elif isinstance(obj, list):
        for i, item in enumerate(obj):
            find_image_with_text(item, path + f'[{i}]', found)

results = []
find_image_with_text(data, found=results)
print(f"Total imageWithText found: {len(results)}")
if results:
    # Print first one
    print("First one:")
    print(json.dumps(results[0][1], indent=2))
