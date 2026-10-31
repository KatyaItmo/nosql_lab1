import {useEffect, useState} from "react";

function ModerPage({ showMessage }) {
    const [isAddingProduct, setIsAddingProduct] = useState(false);
    const [productName, setProductName] = useState('');
    const [productDesc, setProductDesc] = useState('');
    const [productPrice, setProductPrice] = useState('');

    const [orders, setOrders] = useState([]);

    const handlePriceChange = (e) => {
        const value = e.target.value;
        if (/^\d*$/.test(value)) {
            setProductPrice(value);
        }
    };

    const handleAddProduct = async () => {
        if (productName === '' || productDesc === '' || productPrice === '') {
            showMessage('заполните все поля!', 'error');
            return;
        }

        const product = {
            name: productName,
            description: productDesc,
            price: Number(productPrice)
        }

        try {
            const response = await fetch('/api/products/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify(product)
            });

            const resultText = await response.text();

            if (response.ok) {
                showMessage(resultText, 'info');

                setIsAddingProduct(false);
                setProductName('');
                setProductDesc('');
                setProductPrice('');
            } else {
                showMessage(resultText || 'ошибка сервера', 'error');
            }
        } catch (err) {
            showMessage('не удалось отправить данные на сервер', 'error');
        }
    };

    const getOrders = async () => {
        try {
            const response = await fetch('/api/orders/allOrders', {
                method: 'GET',
                credentials: 'include'
            });
            const data = await response.json();
            setOrders(data);
        } catch (err) {
            showMessage('не удалось связаться с сервером', 'error');
        }
    }

    useEffect(() => {
        getOrders().then(r => {});
    }, []);

    const handleModerate = async (orderId, clientName, newStatus) => {
        const data = {
            orderId: orderId,
            clientName: clientName,
            newStatus: newStatus
        }

        try {
            const response = await fetch('api/orders/moderate', {
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
            await getOrders();
        }
    }

    return (
        <div className="page">
            <h2>модерация товаров и заказов</h2>

            <div>
                <button
                    type="button"
                    onClick={() => setIsAddingProduct(true)}
                >
                    добавить продукт
                </button>

                {isAddingProduct && (
                    <form>
                        <input
                            type="text"
                            placeholder="название"
                            value={productName}
                            onChange={(e) => setProductName(e.target.value)}
                        />

                        <input
                            type="text"
                            placeholder="описание"
                            value={productDesc}
                            onChange={(e) => setProductDesc(e.target.value)}
                        />

                        <input
                            type="text"
                            placeholder="цена"
                            value={productPrice}
                            onChange={(e) => handlePriceChange(e)}
                        />

                        <button
                            type="button"
                            onClick={handleAddProduct}
                        >
                            добавить
                        </button>
                    </form>
                )}
            </div>

            <div>
                <button
                    type="button"
                    onClick={getOrders}
                >
                    обновить
                </button>

                <div>
                    {orders.length === 0 && (
                        <p>нет заказов</p>
                    )}

                    {orders.length !== 0 && (orders.map(order => (
                        <div key={order.id} className="card">
                            {(order.status === 'confirmed' || order.status === 'accepted') && (
                                <div>
                                    <div>
                                        <h3>заказ №{order.id}</h3>
                                        <p>клиент: {order.username}</p>
                                        <p>статус заказа: {order.status}</p>
                                        <p>сумма: {order.cost} руб.</p>
                                    </div>

                                    {order.status === 'confirmed' && (
                                        <div>
                                            <button
                                                type="button"
                                                onClick={() => handleModerate(order.id, order.username, 'accepted')}
                                            >
                                                принять
                                            </button>

                                            <button
                                                type="button"
                                                onClick={() => handleModerate(order.id, order.username, 'declined')}
                                            >
                                                отклонить
                                            </button>
                                        </div>
                                    )}

                                    <div>
                                        <h3>товары в заказе</h3>

                                        <table>
                                            <thead>
                                            <tr>
                                                <th>товар</th>
                                                <th>цена</th>
                                                <th>количество</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            {order.orderList.map(item => (
                                                <tr key={item.productId}>
                                                    <td>{item.productName}</td>
                                                    <td>{item.productPrice} руб.</td>
                                                    <td>{item.amount} шт.</td>
                                                </tr>
                                            ))}
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            )}
                        </div>
                    )))}
                </div>
            </div>
        </div>
    );
}

export default ModerPage;