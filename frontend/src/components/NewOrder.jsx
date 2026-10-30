import {useState} from "react";

function NewOrder({ orderId, code, showMessage, setIsConfirmingOrder }) {
    const [userCode, setUserCode] = useState('');

    const handleCodeChange = (e) => {
        const value = e.target.value;
        if (/^\d{0,4}$/.test(value)) {
            setUserCode(value);
        }
    };

    const handleDeletion = async () => {
        const data = {
            orderId: orderId,
            code: 676767
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

    const handleConfirmation = async () => {
        if (userCode === '') {
            showMessage('заполните поле', 'error');
            return;
        }

        const data = {
            orderId: orderId,
            code: Number(userCode)
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

    return (
        <div>
            <h2>подтверждение заказа</h2>

            <form>
                <h3>ID заказа: {orderId}</h3>

                <p>введите этот код ниже для подтверждения заказа: {code}</p>

                <input
                    type="text"
                    placeholder="код подтверждения"
                    value={userCode}
                    onChange={(e) => handleCodeChange(e)}
                />

                <div>
                    <button
                        type="button"
                        onClick={handleConfirmation}
                    >
                        подтвердить
                    </button>

                    <button
                        type="button"
                        onClick={handleDeletion}
                    >
                        отменить заказ
                    </button>
                </div>
            </form>
        </div>
    );
}

export default NewOrder;