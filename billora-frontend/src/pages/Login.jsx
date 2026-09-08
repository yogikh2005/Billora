import { useState } from "react";
import api from "../api/axios";
import { useNavigate } from "react-router-dom";
import "../css/login.css";

function Login() {

    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        username: "",
        password: "",
    });

    const [loading, setLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const [showPassword, setShowPassword] = useState(false);

    const handleChange = (e) => {

        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });

        setErrorMessage("");
    };


    const handleSubmit = async (e) => {

        e.preventDefault();

        setLoading(true);
        setErrorMessage("");

        try {

            const res = await api.post(
                "/auth/signin",
                formData
            );

           sessionStorage.setItem(
            "token",
            res.data.token
             );

            sessionStorage.setItem(
            "user",
            JSON.stringify(res.data.user)
            );

            // Go to dashboard
            navigate("/dashboard");

        } catch (error) {

            console.log(
                "Status:",
                error.response?.status
            );

            console.log(
                "Response:",
                error.response?.data
            );

            if (error.response?.status === 401) {

                setErrorMessage(
                    "Invalid username or password."
                );

            } else if (error.response?.status === 403) {

                setErrorMessage(
                    "Your account is inactive. Please contact the administrator."
                );

            } else {

                setErrorMessage(
                    "Unable to login. Please try again."
                );
            }

        } finally {

            setLoading(false);
        }
    };


    return (

        <div className="login-page">

            {/* =================================
                LEFT BRANDING SECTION
            ================================= */}

            <div className="login-brand">
                <img
                    src="/images/billora-logo.png"
                    alt="Billora GST Billing"
                    className="billora-logo"
                />
            </div>


            {/* =================================
                RIGHT LOGIN SECTION
            ================================= */}

            <div className="login-container">

                <div className="login-title">

                    <h1>
                        Welcome Back
                    </h1>

                    <p>
                        Sign in to your Billora account
                    </p>

                </div>


                {/* Error */}

                {errorMessage && (

                    <div className="login-error">

                        <span>!</span>

                        {errorMessage}

                    </div>

                )}


                <form
                    className="login-form"
                    onSubmit={handleSubmit}
                >

                    {/* Username */}

                    <div className="form-group">

                        <label>
                            Username
                        </label>

                        <input
                            type="text"
                            name="username"
                            placeholder="Enter username"
                            value={formData.username}
                            onChange={handleChange}
                            required
                            autoComplete="username"
                        />

                    </div>


                    {/* Password */}

                    <div className="form-group">

                        <div className="password-label">

                            <label>
                                Password
                            </label>

                        </div>


                        <div className="password-input-wrapper">

                            <input
                                type={
                                    showPassword
                                        ? "text"
                                        : "password"
                                }
                                name="password"
                                placeholder="Enter password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                autoComplete="current-password"
                            />

                            <button
                                type="button"
                                className="show-password-button"
                                onClick={() =>
                                    setShowPassword(
                                        !showPassword
                                    )
                                }
                            >
                                {showPassword
                                    ? "Hide"
                                    : "Show"}
                            </button>

                        </div>

                    </div>

                    {/* Login */}

                    <button
                        className="login-button"
                        type="submit"
                        disabled={loading}
                    >

                        {loading
                            ? "Signing in..."
                            : "Login"}

                    </button>

                </form>


                {/* Footer */}

                <div className="login-footer">

                    <p>
                        Secure GST billing & invoice management
                    </p>

                    <span>
                        © 2026 Billora
                    </span>

                </div>

            </div>

        </div>
    );
}

export default Login;