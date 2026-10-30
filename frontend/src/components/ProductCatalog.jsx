import {useEffect, useState} from "react";

function ProductCatalog({ userRole, showMessage }) {
    const [products, setProducts] = useState([]);
    const [quantities, setQuantities] = useState({});
    const [costs, setCosts] = useState({});

    const getProducts = async () => {
        try {
            const response = await fetch('/api/products', {
                method: 'GET',
                credentials: 'include'
            });
            const data = await response.json();
            setProducts(data);
        } catch (err) {
            showMessage('ошибка получения данных с сервера', 'error');
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
        const newCost = products.find(product => product.id === productId).price * currQuantity;

        setCosts({
            ...costs,
            [productId]: newCost
        });
    };

    const handleAddItem = async (productId) => {
        if (!quantities[productId] || quantities[productId] === 0) {
            showMessage('вы выбрали 0 товаров!', 'error')
            return;
        }

        const product = products.find(product => product.id === productId);

        const data = {
            product,
            amount: quantities[productId],
            cost: costs[productId]
        }

        try {
            const response = await fetch('/api/cart/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(data)
            });

            const resultText = await response.text();

            if (response.ok) {
                showMessage(resultText, 'info');
                resetStats(productId);
            } else {
                showMessage(resultText || 'ошибка сервера', 'error');
            }
        } catch(err) {
            showMessage('не удалось отправить данные', 'error');
        }
    }

    const resetStats = (productId) => {
        setQuantities({
            ...quantities,
            [productId]: 0
        });

        setCosts({
            ...costs,
            [productId]: 0
        });
    }

    return (
        <div>
            <h2>каталог товаров</h2>

            <button
                type="button"
                onClick={getProducts}
            >
                обновить список
            </button>

            <div>
                {products.length === 0 && (
                    <p>у нас пока нет товаров, мы бомжи</p>
                )}

                {products.length !== 0 && (products.map(product => (
                    <div key={product.id} >
                        <h3>{product.name}</h3>
                        <p>{product.description}</p>
                        <p>Цена за шт. {product.price} руб.</p>

                        {userRole === 'user' ? (
                            <div>
                                <button
                                    type="button"
                                    onClick={() => handleDecrease(product.id)}
                                >
                                    -
                                </button>

                                <p>{quantities[product.id] || 0}</p>

                                <button
                                    type="button"
                                    onClick={() => handleIncrease(product.id)}
                                >
                                    +
                                </button>

                                <p>Сумма: {costs[product.id] || 0} руб.</p>

                                <button
                                    type="button"
                                    onClick={() => handleAddItem(product.id)}
                                >
                                    добавить в корзину
                                </button>
                            </div>
                        ) : userRole === 'moder' ? (
                            <div></div>
                        ): (
                            <p>войдите, чтобы сделать заказ</p>
                        )}
                    </div>
                )))}
            </div>

        </div>
    );
}

export default ProductCatalog;