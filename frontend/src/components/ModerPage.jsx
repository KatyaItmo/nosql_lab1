import {useEffect, useState} from "react";

function ModerPage({ showMessage }) {
    const [isAddingProduct, setIsAddingProduct] = useState(false);
    const [productName, setProductName] = useState('');
    const [productDesc, setProductDesc] = useState('');
    const [productPrice, setProductPrice] = useState('');

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

    return (
        <div>
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
        </div>
    );
}

export default ModerPage;