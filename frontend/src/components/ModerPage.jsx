import {useEffect, useState} from "react";

function ModerPage({ showMessage }) {
    const [isAddingProduct, setIsAddingProduct] = useState(false);
    const [nameProduct, setNameProduct] = useState('');
    const [descProduct, setDescProduct] = useState('');
    const [priceProduct, setPriceProduct] = useState('');

    const [orders, setOrders] = useState([]);
    const [statuses, setStatuses] = useState({});


    const handlePriceChange = (e) => {
        const value = e.target.value;
        if (/^\d*$/.test(value)) {
            setPriceProduct(value);
        }
    };

    const handleAdd = async () => {
        if (nameProduct === '' || descProduct === '' || priceProduct === '') {
            showMessage('заполните все поля!', 'error');
            return;
        }

        const product = {
            name: nameProduct,
            description: descProduct,
            price: Number(priceProduct)
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
                setNameProduct('');
                setDescProduct('');
                setPriceProduct('');
            } else {
                showMessage(resultText || 'ошибка сервера', 'error');
            }
        } catch (err) {
            showMessage('не удалось связаться с сервером', 'error');
        }
    };

    const getOrders = async () => {
        try {
            const response = await fetch('/api/orders/allOrders', {method: 'GET'});
            const data = await response.json();
            setOrders(data);
        } catch (err) {
            showMessage('не удалось связаться с сервером', 'error');
        }
    };

    const acceptedStatus = async (orderID) => {
        setStatuses({
            ...statuses,
            [orderID]: 'accepted'
        });
        await changeStatus(orderID);
    };

    const declinedStatus = async (orderID) => {
        setStatuses({
            ...statuses,
            [orderID]: 'declined'
        });
        await changeStatus(orderID);
    };

    useEffect(() => {
        getOrders().then(r => {});
    }, []);

    const changeStatus = async (orderID) => {
        const data = {
            orderID: orderID,
            username: orders.find(order => order.orderID === orderID).username,
            newStatus: statuses[orderID]
        }

        try {
            const response = await fetch('/api/orders/moderate', {
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
                showMessage(resultText || 'ошибка сервера', 'error');
            }
        } catch (err) {
            showMessage('не удалось связаться с сервером', 'error');
        }
    };

    return (
        <div className="page">
            <div className="moder-block" id="add-block">
                <button
                    className="addProd-but"
                    type="button"
                    onClick={() => setIsAddingProduct(true)}
                >
                    добавить продукт
                </button>

                {isAddingProduct && (
                    <form>
                        <input
                            className="addProd-input"
                            type="text"
                            placeholder="название"
                            value={nameProduct}
                            onChange={(e) => setNameProduct(e.target.value)}
                        />

                        <input
                            className="addProd-input"
                            type="text"
                            placeholder="описание"
                            value={descProduct}
                            onChange={(e) => setDescProduct(e.target.value)}
                        />

                        <input
                            className="addProd-input"
                            type="text"
                            placeholder="цена"
                            value={priceProduct}
                            onChange={(e) => handlePriceChange(e)}
                        />

                        <button
                            className="addProd-but"
                            type="button"
                            onClick={handleAdd}
                        >
                            добавить
                        </button>
                    </form>
                )}
            </div>

            <div className="moder-block" id="orders-block">
                <h2>модерация заказов</h2>

                <button
                    className="simple-button"
                    type="button"
                    onClick={getOrders}
                >
                    обновить список
                </button>

                <div className="list">
                    {orders.length === 0 && (
                        <p className="big-p">нет заказов для модерации</p>
                    )}

                    {orders.length !== 0 && (orders.map(order => (
                        <div key={order.OrderID}>
                            <h3>заказ {order.orderID}</h3>
                            <p className="order-field" id="order-status">статус заказа: {order.status}</p>
                            <p className="order-field">имя клиента: {order.username}</p>
                            <p className="order-field">товар: {order.nameProduct}</p>
                            <p className="order-field">количество: {order.amount}</p>
                            <p className="order-field">стоимость: {order.cost}</p>

                            {order.status === 'confirmed' && (
                                <div>
                                    <button
                                        type="button"
                                        onClick={() => acceptedStatus(order.orderID)}
                                    >
                                        принять
                                    </button>

                                    <button
                                        type="button"
                                        onClick={() => declinedStatus(order.orderID)}
                                    >
                                        отклонить
                                    </button>
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