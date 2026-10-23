import {useEffect, useState} from "react";

function ProductCatalog({ userRole, showMessage }) {
    const [products, setProducts] = useState([]);
    const [quantities, setQuantities] = useState({});
    const [costs, setCosts] = useState({});

    const getProducts = async () => {
        try {
            const response = await fetch('/api/products', {method: 'GET'});
            const data = await response.json();
            setProducts(data);
        } catch (err) {
            showMessage('не удалось связаться с сервером', 'error');
        }
    };

    useEffect(() => {
        getProducts().then(r => {});
    }, []);

    const handleIncrease = (productId) => {
        const currQuantity = quantities[productId] || 0;
        setQuantities({
            ...quantities,
            [productId]: currQuantity + 1
        });

        handleCosts(productId, currQuantity + 1);
    };

    const handleDecrease = (productId) => {
        const currQuantity = quantities[productId] || 0;

        if (currQuantity > 0) {
            setQuantities({
                ...quantities,
                [productId]: currQuantity - 1
            });

            handleCosts(productId, currQuantity - 1);
        }
    };

    const handleCosts = (productId, currQuantity) => {
        const newCost = products.find(product => product.productID === productId).price * currQuantity;

        setCosts({
            ...costs,
            [productId]: newCost
        });
    };

    const handleOrder = async (productID) => {
        const amount = quantities[productID] || 0;
        if (amount === 0) {
            showMessage('нельзя заказать 0 товаров', 'error');
            return;
        }

        const order = {
            idProduct: productID,
            nameProduct: products.find(product => product.productID === productID).name,
            amount: amount,
            cost: costs[productID]
        }

        try {
            const response = await fetch('/api/orders/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(order)
            });

            const resultText = await response.text();

            if (response.ok) {
                showMessage(resultText, 'info');
            } else {
                showMessage(resultText || 'ошибка сервера', 'error');
            }
        } catch (err) {
            showMessage('не удалось связаться с сервером', 'error');
        }
    };

    return (
        <div className="page">
            <h2>каталог товаров</h2>

            <button
                className="simple-button"
                type="button"
                onClick={getProducts}
            >
                обновить список
            </button>

            <div className="list" id="products">
                {products.length === 0 && (
                    <p className="big-p">у нас пока нет товаров, мы бомжи</p>
                )}

                {products.length !== 0 && (products.map(product => (
                    <div key={product.productID} className="card" id="product-card">
                        <h3>{product.name}</h3>
                        <p className="prod-desc">{product.description}</p>
                        <p className="prod-price">Цена за шт. {product.price} руб.</p>

                        {userRole === 'moder' ? (
                            <p className="mid-p">вы модератор</p>
                        ) : userRole === 'user' ? (
                            <div>
                                <button
                                    className="ammount-prod-but"
                                    type="button"
                                    onClick={() => handleDecrease(product.productID)}
                                >
                                    -
                                </button>

                                <p className="order-amount">{quantities[product.productID] || 0}</p>

                                <button
                                    className="ammount-prod-but"
                                    type="button"
                                    onClick={() => handleIncrease(product.productID)}
                                >
                                    +
                                </button>

                                <p className="order-cost">всего {costs[product.productID] || 0} руб.</p>

                                <button
                                    className="order-make-but"
                                    type="button"
                                    onClick={() => handleOrder(product.productID)}
                                >
                                    заказать
                                </button>
                            </div>
                        ) : (
                            <p className="mid-p">войдите, чтобы сделать заказ</p>
                        )}
                    </div>
                )))}
            </div>

        </div>
    );
}

export default ProductCatalog;