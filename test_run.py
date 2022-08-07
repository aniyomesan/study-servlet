import pytest
import requests

BASE_URL = 'http://localhost:8080/api/v1'


@pytest.fixture(autouse=True)
def cleanup():
    code, items = call_find_all()
    assert code == 200
    for item in items:
        c, _ = call_delete(item['id'])
        assert c == 204


def test_api():
    # empty.
    code, items = call_find_all()
    assert code == 200
    assert items == []

    # create.
    item1 = {'title': 'Hello, ToDo app.', 'done': False}
    code, created1 = call_create(item1)
    assert code == 200
    assert created1['title'] == item1['title']
    assert not created1['done']

    # find one.
    item1_id = created1['id']
    code, found1 = call_find_one(item1_id)
    assert code == 200
    assert found1 == created1

    # update.
    found1['title'] = 'Updated ToDo item'
    found1['done'] = True
    code, updated1 = call_update(found1)
    assert code == 200
    assert updated1 == found1

    # create second one.
    item2 = {'title': 'Item #2.', 'done': True}
    code, created2 = call_create(item2)
    assert code == 200
    assert created2['title'] == item2['title']
    assert created2['done']

    # find all.
    code, items = call_find_all()
    assert code == 200
    assert len(items) == 2
    assert updated1 in items
    assert created2 in items

    # delete.
    code, _ = call_delete(item1_id)
    assert code == 204

    code, items = call_find_all()
    assert code == 200
    assert len(items) == 1
    assert updated1 not in items
    assert created2 in items


def test_400():
    item = {'title': 'Hello, ToDo app.', 'done': False}
    code, created = call_create(item)
    assert code == 200

    item_id = created['id'] + 1
    response = requests.put(f'{BASE_URL}/todos/{item_id}', json=created)
    assert response.status_code == 400


def test_404():
    item = {'title': 'Hello, ToDo app.', 'done': False}
    code, created = call_create(item)
    assert code == 200
    item_id = created['id']

    code, _ = call_find_one(item_id + 1)
    assert code == 404

    item['id'] = item_id + 1
    code, _ = call_update(item)
    assert code == 404

    code, _ = call_delete(item_id + 1)
    assert code == 404


def call_find_all():
    response = requests.get(f'{BASE_URL}/todos')
    return (response.status_code, response.json())


def call_find_one(item_id):
    response = requests.get(f'{BASE_URL}/todos/{item_id}')
    return (response.status_code, response.json())


def call_create(item):
    response = requests.post(f'{BASE_URL}/todos', json=item)
    return (response.status_code, response.json())


def call_update(item):
    item_id = item['id']
    response = requests.put(f'{BASE_URL}/todos/{item_id}', json=item)
    return (response.status_code, response.json())


def call_delete(item_id):
    response = requests.delete(f'{BASE_URL}/todos/{item_id}')
    return (response.status_code, None)
