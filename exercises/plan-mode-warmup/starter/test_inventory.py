"""Basic tests for inventory module - some are missing, some will fail."""

import pytest

from inventory import (
    add_item,
    apply_discount,
    find_low_stock,
    generate_report,
    get_total_value,
    remove_item,
    restock,
)


def test_add_item():
    inv = {}
    add_item(inv, "Widget", 10, 2.50)
    assert "Widget" in inv
    assert inv["Widget"]["quantity"] == 10


def test_add_item_rejects_negative_values():
    inv = {}
    with pytest.raises(ValueError):
        add_item(inv, "Widget", -1, 2.50)
    with pytest.raises(ValueError):
        add_item(inv, "Widget", 1, -2.50)


def test_total_value():
    inv = {}
    add_item(inv, "Widget", 10, 2.50)
    add_item(inv, "Gadget", 5, 10.00)
    assert get_total_value(inv) == 75.00


def test_total_value_empty_inventory():
    assert get_total_value({}) == 0


def test_find_low_stock():
    inv = {}
    add_item(inv, "Widget", 3, 2.50)
    add_item(inv, "Gadget", 50, 10.00)
    low = find_low_stock(inv, 5)
    assert "Widget" in low
    assert "Gadget" not in low


def test_find_low_stock_empty_inventory():
    assert find_low_stock({}, 5) == []


def test_report_contains_header():
    inv = {}
    add_item(inv, "Widget", 10, 2.50)
    report = generate_report(inv)
    assert "Inventory Report" in report


def test_report_handles_empty_inventory():
    report = generate_report({})
    lines = report.splitlines()
    assert lines[0] == "=== Inventory Report ==="
    assert lines[-1].endswith("0.00")


def test_remove_item_missing_is_safe():
    inv = {}
    remove_item(inv, "Missing")
    assert inv == {}


def test_apply_discount_updates_price_and_handles_boundaries():
    inv = {}
    add_item(inv, "Widget", 10, 100.0)

    apply_discount(inv, "Widget", 50)
    assert inv["Widget"]["price"] == 50.0

    apply_discount(inv, "Widget", 0)
    assert inv["Widget"]["price"] == 50.0

    apply_discount(inv, "Widget", 100)
    assert inv["Widget"]["price"] == 0.0


def test_apply_discount_rejects_invalid_values():
    inv = {}
    add_item(inv, "Widget", 10, 100.0)

    with pytest.raises(ValueError):
        apply_discount(inv, "Widget", -1)

    with pytest.raises(ValueError):
        apply_discount(inv, "Widget", 101)


def test_apply_discount_missing_item_raises_key_error():
    with pytest.raises(KeyError):
        apply_discount({}, "Missing", 10)


def test_restock_updates_existing_items():
    inv = {}
    add_item(inv, "Widget", 10, 2.50)

    restock(inv, "Widget", 5)

    assert inv["Widget"]["quantity"] == 15


def test_restock_rejects_invalid_or_missing_items():
    inv = {}
    add_item(inv, "Widget", 10, 2.50)

    with pytest.raises(ValueError):
        restock(inv, "Widget", -1)

    with pytest.raises(KeyError):
        restock({}, "Missing", 1)
