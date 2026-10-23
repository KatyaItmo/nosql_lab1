import {useEffect, useState} from "react";

function UserOrders({ showMessage }) {
    const [userOrders, setUserOrders] = useState([]);

    const getOrders = async () => {
        try {
            const response = await fetch('/api/orders/userOrders', {method: 'GET'});
            const data = await response.json();
            setUserOrders(data);
        } catch (err) {
            showMessage('не удалось связаться с сервером', 'error');
        }
    };

    useEffect(() => {
        getOrders();
    }, []);

    return (
        <div className="page">
            <h2>мои заказы</h2>

            <button
                className="simple-button"
                type="button"
                onClick={getOrders}
            >
                обновить
            </button>

            <div className="list">
                {userOrders.length === 0 && (
                    <p className="big-p">вы еще ничего у нас не заказывали</p>
                )}

                {userOrders.length !== 0 && (userOrders.map(order => (
                    <div key={order.orderID} className="card" id="order-card">
                        <h3>заказ {order.orderID}</h3>
                        <p className="order-field" id="order-status">статус заказа: {order.status}</p>
                        <p className="order-field">товар: {order.nameProduct}</p>
                        <p className="order-field">количество: {order.amount}</p>
                        <p className="order-field">стоимость: {order.cost}</p>
                    </div>
                )))}
            </div>
        </div>
    );
}

export default UserOrders;