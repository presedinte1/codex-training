"""Simple inventory management module with a few issues to fix."""


def _validate_non_negative(value, field_name):
    if value < 0:
        raise ValueError(f"{field_name} must be non-negative")


def _get_existing_item(inventory, name):
    try:
        return inventory[name]
    except KeyError:
        raise KeyError(name) from None


def add_item(inventory, name, quantity, price):
    """Add an item to inventory. Overwrites if item already exists."""
    _validate_non_negative(quantity, "quantity")
    _validate_non_negative(price, "price")
    inventory[name] = {"quantity": quantity, "price": price}


def remove_item(inventory, name):
    """Remove an item from inventory."""
    inventory.pop(name, None)


def get_total_value(inventory):
    """Calculate total value of all items in inventory."""
    total = 0
    for item in inventory:
        total += inventory[item]["quantity"] * inventory[item]["price"]
    return total


def apply_discount(inventory, name, percent):
    """Apply a percentage discount to an item's price."""
    _validate_non_negative(percent, "percent")
    if percent > 100:
        raise ValueError("percent must not exceed 100")
    item = _get_existing_item(inventory, name)
    item["price"] = item["price"] - (item["price"] * (percent / 100))


def find_low_stock(inventory, threshold):
    """Find items with quantity below threshold."""
    if not inventory:
        return []
    results = []
    for name in inventory:
        if inventory[name]["quantity"] < threshold:
            results.append(name)
    return results


def restock(inventory, name, amount):
    """Add stock to an existing item."""
    _validate_non_negative(amount, "amount")
    item = _get_existing_item(inventory, name)
    item["quantity"] = item["quantity"] + amount


def generate_report(inventory):
    """Generate a simple text report of inventory."""
    lines = []
    lines.append("=== Inventory Report ===")
    lines.append(f"{'Item':<20} {'Qty':>5} {'Price':>8} {'Value':>10}")
    lines.append("-" * 45)
    for name in sorted(inventory.keys()):
        item = inventory[name]
        value = item["quantity"] * item["price"]
        lines.append(f"{name:<20} {item['quantity']:>5} {item['price']:>8.2f} {value:>10.2f}")
    lines.append("-" * 45)
    lines.append(f"{'Total':<20} {'':>5} {'':>8} {get_total_value(inventory):>10.2f}")
    return "\n".join(lines)
