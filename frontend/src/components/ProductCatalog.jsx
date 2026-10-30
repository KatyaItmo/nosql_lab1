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