interface IShoppingCart {
    void addItem();
    void deleteItem();
    void updateShoppingCart();
    void deleteShoppingCart();
    void computeTotalAmount();
    void computeItemAmount();
}

abstract class Order {
    abstract void processOrder();
    abstract void computeTotalFee();
    abstract void updateOrder();
    abstract void cancelOrder();
    abstract void searchOrder();
    abstract void returnOrder();
}

class ShoppingCartA implements IShoppingCart {
    @Override
    public void addItem() {}

    @Override
    public void deleteItem() {}

    @Override
    public void updateShoppingCart() {}

    @Override
    public void deleteShoppingCart() {}

    @Override
    public void computeTotalAmount() {}

    @Override
    public void computeItemAmount() {}
}

class ShoppingCartB implements IShoppingCart {
    @Override
    public void addItem() {}

    @Override
    public void deleteItem() {}

    @Override
    public void updateShoppingCart() {}

    @Override
    public void deleteShoppingCart() {}

    @Override
    public void computeTotalAmount() {}

    @Override
    public void computeItemAmount() {}
}

class ShoppingCartC implements IShoppingCart {
    @Override
    public void addItem() {}

    @Override
    public void deleteItem() {}

    @Override
    public void updateShoppingCart() {}

    @Override
    public void deleteShoppingCart() {}

    @Override
    public void computeTotalAmount() {}

    @Override
    public void computeItemAmount() {}
}
