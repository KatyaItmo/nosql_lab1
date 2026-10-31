import {useEffect, useState} from "react";
import NewOrder from "./NewOrder.jsx";

function CartPage({ showMessage }) {
    const [cartItems, setCartItems] = useState([]);
    const [isConfirmingOrder, setIsConfirmingOrder] = useState(false);
    const [confirmCode, setConfirmCode] = useState(0);
    const [newOrderId, setNewOrderId] = useState('');

    const getCart = async () => {
        try {
            const response = await fetch('/api/cart', {
                method: 'GET',
                credentials: 'include'
            });
            const data = await response.json();
            if (data === null) {
                showMessage('получение данных о корзине запрещено', 'error');
            }
            setCartItems(data);
        } catch (err) {
            showMessage('ошибка получения данных с сервера', 'error');
        }
    }

    useEffect(() => {
        getCart().then(r => {});
    }, []);

    const handleDeleteItem = async (productId) => {
        try {
            const response = await fetch(`/api/cart/delete/${productId}`, {
                method: 'GET',
                credentials: 'include'
            });
            const resultText = await response.text();

            if (response.ok) {
                showMessage(resultText, 'info');
                await getCart();
            } else {
                showMessage(resultText || 'ошибка сервера', 'error');
            }
        } catch (err) {
            showMessage('ошибка отправки запроса на сервер', 'error');
        }
    }

    const handleClear = async () => {
        try {
            const response = await fetch('/api/cart/clear', {
                method: 'GET',
                credentials: 'include'
            });
            const resultText = await response.text();

            if (response.ok) {
                showMessage(resultText, 'info');
                await getCart();
            } else {
                showMessage(resultText || 'ошибка сервера', 'error');
            }
        } catch (err) {
            showMessage('ошибка отправки запроса на сервер', 'error');
        }
    }

    const calculateSum = () => {
        return cartItems.reduce((sum, item) => {
            return sum + item.cost;
        }, 0);
    }

    const handleOrder = async () => {
        if (cartItems.length === 0) {
            showMessage('сначала выберете товары и положите в корзину', 'text');
            return;
        }

        const orderList = cartItems.map(item => {
            return {
                productId: item.product.id,
                productName: item.product.name,
                productPrice: item.product.price,
                amount: item.amount
            };
        })

        const orderData = {
            orderList: orderList,
            cost: calculateSum()
        }

        try {
            const response = await fetch('/api/orders/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(orderData)
            });

            const responseData = await response.json();

            if (response.ok) {
                showMessage(responseData.message, 'info');
                await getCart();
                setNewOrderId(responseData.orderId);
                setConfirmCode(responseData.code);
                setIsConfirmingOrder(true);
            } else {
                showMessage(responseData.message || 'ошибка сервера', 'error');
            }
        } catch(err) {
            showMessage('не удалось отправить данные', 'error');
        }
    }

    const handleTimeoutAction = async () => {
        const data = {
            orderId: newOrderId,
            code: 198467
        }

        try {
            const response = await fetch('api/orders/confirm', {
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
            } else {
                showMessage(resultText, 'error');
            }
        } catch(err) {
            showMessage('ошибка отправки данных', 'error');
        } finally {
            setIsConfirmingOrder(false);
        }
    }

    useEffect(() => {
        let timer = null;

        if (isConfirmingOrder) {
            timer = setTimeout(() => {
                handleTimeoutAction().then(r => {});
            }, 180000);
        }

        return () => {
            if (timer) {
                clearTimeout(timer);
            }
        };
    }, [isConfirmingOrder]);

    return (
        <div className="page">
            {isConfirmingOrder ? (
                <NewOrder orderId={newOrderId} code={confirmCode} showMessage={showMessage} setIsConfirmingOrder={setIsConfirmingOrder} />
            ) : (
                <div>
                    <h2>корзина</h2>

                    <div>
                        <button
                            type="button"
                            onClick={getCart}
                        >
                            обновить список
                        </button>

                        <button
                            type="button"
                            onClick={handleClear}
                        >
                            очистить корзину
                        </button>
                    </div>

                    <div>
                        {cartItems.length === 0 && (
                            <p>корзина пуста</p>
                        )}

                        {cartItems.length !== 0 && (cartItems.map(item => (
                            <div key={item.id} className="card">
                                <div>
                                    <h3>{item.product.name}</h3>
                                    <p>количество: {item.amount}</p>
                                    <p>сумма: {item.cost}</p>
                                </div>

                                <button
                                    type="button"
                                    onClick={() => handleDeleteItem(item.product.id)}
                                >
                                    убрать из корзины
                                </button>
                            </div>
                        )))}
                    </div>

                    <div>
                        <p>Всего: {calculateSum()}</p>

                        <button
                            type="button"
                            onClick={handleOrder}
                        >
                            сделать заказ
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default CartPage;