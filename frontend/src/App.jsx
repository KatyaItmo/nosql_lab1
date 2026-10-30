import Header from "./components/Header";
import Footer from "./components/Footer";
import {useState} from "react";
import LoginPage from "./components/LoginPage.jsx";
import ProductCatalog from "./components/ProductCatalog.jsx";
import ModerPage from "./components/ModerPage.jsx";
import UserOrders from "./components/UserOrders.jsx";
import CartPage from "./components/CartPage.jsx";

function App() {
    const [currentPage, setCurrentPage] = useState("main");
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userRole, setUserRole] = useState('none');

    const [message, setMessage] = useState({text: '', type: ''});


    const showMessage = (text, type) => {
        setMessage({text, type});
        setTimeout(() => {
            setMessage({text: '', type: ''});
        }, 4000);
    };

    const handleLoginSuccess = (role) => {
        setIsLoggedIn(true);
        setUserRole(role);
        setCurrentPage("main");
    };

    const handleLogout = async () => {
        try {
            const response = await fetch("/api/auth/logout", {
                method: 'POST',
                credentials: 'include',
            });

            const resultText = await response.text();

            if (response.ok) {
                showMessage(resultText, 'info');

                setIsLoggedIn(false);
                setUserRole("none");
                setCurrentPage("login");

            } else {
                showMessage(resultText || 'ошибка сервера', 'error');
            }
        } catch (err) {
            showMessage('не удалось связаться с сервером', 'error');
        }
    };

    return (
        <div>
            <Header
                isLoggedIn={isLoggedIn}
                userRole={userRole}
                switchPage={setCurrentPage}
                onLogout={handleLogout}
            />

            {message.text && (
                <div className={`message ${message.type}`}>
                    {message.text}
                </div>
            )}

            <main>
                {currentPage === "login" ? (
                    <LoginPage
                        onLoginSuccess={handleLoginSuccess}
                        showMessage={showMessage}
                    />
                ) : currentPage === "cart" ? (
                    <CartPage />
                ) : currentPage === "my-orders" ? (
                    <UserOrders showMessage={showMessage} />
                ) : currentPage === "moder-tools" ? (
                    <ModerPage showMessage={showMessage} />
                ) : (
                    <ProductCatalog
                        userRole={userRole}
                        showMessage={showMessage}
                    />
                )}
            </main>

            <Footer />
        </div>
    );
}

export default App;