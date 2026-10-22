import {useState} from "react";

function ModerPage({ showMessage }) {
    const [isAddingProduct, setIsAddingProduct] = useState(false);
    const [nameProduct, setNameProduct] = useState('');
    const [descProduct, setDescProduct] = useState('');
    const [costProduct, setCostProduct] = useState('');

    const handleCostChange = (e) => {
        const value = e.target.value;
        if (/^\d*$/.test(value)) {
            setCostProduct(value);
        }
    };

    const handleAdd = async () => {
        if (nameProduct === '' || descProduct === '' || costProduct === '') {
            showMessage('заполните все поля!', 'error');
            return;
        }

        const product = {
            name: nameProduct,
            description: descProduct,
            cost: Number(costProduct)
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
                setCostProduct('');
            } else {
                showMessage(resultText || 'ошибка сервера', 'error');
            }
        } catch (err) {
            showMessage('не удалось связаться с сервером', 'error');
        }
    };

    return (
        <div>
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
                        value={costProduct}
                        onChange={(e) => handleCostChange(e)}
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
    );
}

export default ModerPage;