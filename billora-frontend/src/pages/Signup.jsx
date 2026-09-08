import { useState, useEffect } from "react";
import api from "../api/axios";
import { useNavigate } from "react-router-dom";
import "../css/signup.css";

function Setup() {

    const navigate = useNavigate();


    // =========================
    // Checking Setup
    // =========================

    const [checkingSetup, setCheckingSetup] = useState(true);


    // =========================
    // Form Data
    // =========================

    const [formData, setFormData] = useState({
        username: "",
        email: "",
        password: "",
    });


    // =========================
    // Popup
    // =========================

    const [popup, setPopup] = useState({
        show: false,
        type: "success",
        message: ""
    });


    // =========================
    // Check Setup Status
    // =========================

    useEffect(() => {

        const checkSetup = async () => {

            try {

                const res = await api.get("/setup/status");

                console.log(
                    "Setup Status:",
                    res.data
                );


                // =========================
                // Already Setup
                // =========================

                if (res.data === "SETUP_COMPLETED") {

                    navigate("/login");

                    return;
                }


            } catch (error) {

                console.error(
                    "Setup status check failed:",
                    error
                );

            } finally {

                setCheckingSetup(false);

            }

        };


        checkSetup();

    }, [navigate]);


    // =========================
    // Show Popup
    // =========================

    const showPopup = (
        message,
        type = "success"
    ) => {

        setPopup({
            show: true,
            type,
            message
        });

    };


    // =========================
    // Close Popup
    // =========================

    const closePopup = () => {

        const popupType = popup.type;

        setPopup({
            show: false,
            type: "success",
            message: ""
        });


        // Success → Login page

        if (popupType === "success") {

            navigate("/login");

        }

    };


    // =========================
    // Input Change
    // =========================

    const handleChange = (e) => {

        setFormData({

            ...formData,

            [e.target.name]:
                e.target.value

        });

    };


    // =========================
    // Submit Setup
    // =========================

    const handleSubmit = async (e) => {

        e.preventDefault();


        try {

            console.log(
                "Setup Data:",
                formData
            );


            // =========================
            // Create Owner
            // =========================

            const res = await api.post(
                "/setup",
                formData
            );


            console.log(
                "Setup Response:",
                res.data
            );


            showPopup(
                "Owner account created successfully. Please login.",
                "success"
            );


        } catch (error) {

            console.error(error);


            console.log(
                error.response?.data
            );


            console.log(
                error.response?.status
            );


            const message =
                error.response?.data?.message ||
                error.response?.data ||
                "Setup failed. Please try again.";


            showPopup(
                message,
                "error"
            );

        }

    };


    // =========================
    // Loading
    // =========================

    if (checkingSetup) {

        return (

            <div className="signup-page">

                <div className="signup-container">

                    <div className="signup-title">

                        <h1>
                            GST Billing & Invoice Software
                        </h1>

                        <p>
                            Checking application setup...
                        </p>

                    </div>

                </div>

            </div>

        );

    }


    // =========================
    // Page
    // =========================

    return (

        <div className="signup-page">

            <div className="signup-container">


                {/* =========================
                    Title
                ========================= */}

                <div className="signup-title">

                    <h1>
                        GST Billing & Invoice Software
                    </h1>

                    <p>
                        Initial Owner Setup
                    </p>

                </div>


                {/* =========================
                    Form
                ========================= */}

                <form
                    className="signup-form"
                    onSubmit={handleSubmit}
                >


                    {/* Username */}

                    <label>
                        Owner Username
                    </label>

                    <input
                        type="text"
                        name="username"
                        placeholder="Enter owner username"
                        value={formData.username}
                        onChange={handleChange}
                        required
                    />


                    {/* Email */}

                    <label>
                        Owner Email
                    </label>

                    <input
                        type="email"
                        name="email"
                        placeholder="Enter owner email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />


                    {/* Password */}

                    <label>
                        Password
                    </label>

                    <input
                        type="password"
                        name="password"
                        placeholder="Create password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />


                    {/* Button */}

                    <button
                        type="submit"
                        className="signup-button"
                    >
                        Create Owner
                    </button>


                </form>

            </div>


            {/* =========================
                SUCCESS / ERROR POPUP
            ========================= */}

            {popup.show && (

                <div className="popup-overlay">

                    <div
                        className={`popup-box ${popup.type}`}
                    >


                        {/* Icon */}

                        <div className="popup-icon">

                            {popup.type === "success"
                                ? "✓"
                                : "!"
                            }

                        </div>


                        {/* Title */}

                        <h3>

                            {popup.type === "success"
                                ? "Setup Complete"
                                : "Setup Failed"
                            }

                        </h3>


                        {/* Message */}

                        <p>
                            {popup.message}
                        </p>


                        {/* Button */}

                        <button
                            type="button"
                            className="popup-button"
                            onClick={closePopup}
                        >
                            OK
                        </button>


                    </div>

                </div>

            )}

        </div>

    );

}

export default Setup;