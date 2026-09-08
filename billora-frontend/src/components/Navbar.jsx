import { NavLink } from "react-router-dom";

function Navbar() {
    const currentDate = new Date();
    const user = JSON.parse(sessionStorage.getItem("user"));

    return (
        <header className="navbar">

            <div className="navbar-left">

                <p>
                    {currentDate.toLocaleDateString("en-US", {
                        weekday: "long",
                        year: "numeric",
                        month: "long",
                        day: "numeric",
                    })}
                </p>


            </div>


            <div className="navbar-right">

                <button className="notification">
                    🔔
                    <span>3</span>
                </button>


                <div className="profile">

                    <div className="user-avatar">

                        {user.username
                            ?.charAt(
                                0
                            )
                            .toUpperCase()}

                    </div>

                    <div>
                        <strong>{user?.username}</strong>
                        <small>{user?.role}</small>
                    </div>

                </div>

            </div>

        </header>
    );
}

export default Navbar;