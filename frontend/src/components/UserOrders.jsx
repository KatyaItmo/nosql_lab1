import {useEffect, useState} from "react";

function UserOrders({ showMessage }) {
    const [userOrders, setUserOrders] = useState([]);

    const getOrders = async () => {
        try {
            const response = await fetch('/api/orders/userOrders', {
                method: 'GET',
                credentials: 'include'
            });
            const data = await response.json();
            setUserOrders(data);
        } catch (err) {
            showMessage('не удалось связаться с сервером', 'error');
        }
    };

    useEffect(() => {
        getOrders().then(r => {});
    }, []);

    return (
        <div className="page">
            <h2>мои заказы</h2>

            <button
                type="button"
                onClick={getOrders}
            >
                обновить
            </button>

            <div>
                {userOrders.length === 0 && (
                    <p>вы еще ничего у нас не заказывали</p>
                )}

                {userOrders.length !== 0 && (userOrders.map(order => (
                    <div key={order.id} className="card">
                        <div>
                            <h3>заказ №{order.id}</h3>
                            <p>статус заказа: {order.status}</p>
                            <p>сумма: {order.cost} руб.</p>
                        </div>

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
                )))}
            </div>
        </div>
    );
}

export default UserOrders;