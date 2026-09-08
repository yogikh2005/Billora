import { useEffect, useState } from "react";
import api from "../api/axios";
import "../css/OwnerProfile.css";

function OwnerProfile() {

    const [form, setForm] = useState({
        username: "",
        email: "",
        password: ""
    });

    const [showPassword, setShowPassword] = useState(false);

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [message, setMessage] = useState("");
    const [messageType, setMessageType] = useState("");


    // =========================
    // Load Profile
    // =========================

    useEffect(() => {
        loadProfile();
    }, []);


    const loadProfile = async () => {

        try {

            const res = await api.get("/owner/profile");
            console.log(res)
            setForm({
                username: res.data.username || "",
                email: res.data.email || "",
                password: ""
            });

        } catch (error) {

            console.error(error);

            setMessage(
                error.response?.data?.message ||
                "Failed to load profile."
            );

            setMessageType("error");

        } finally {

            setLoading(false);

        }
    };


    // =========================
    // Handle Change
    // =========================

    const handleChange = (e) => {

        setForm(prev => ({
            ...prev,
            [e.target.name]: e.target.value
        }));

    };


    // =========================
    // Submit
    // =========================

    const handleSubmit = async (e) => {

        e.preventDefault();

        setSaving(true);
        setMessage("");
        setMessageType("");


        try {

            const payload = {
                username: form.username,
                email: form.email
            };


            // Only send password if user entered one
            if (form.password.trim() !== "") {
                payload.password = form.password;
            }


            const res = await api.put(
                "/owner/profile",
                payload
            );


            setForm({
                username: res.data.username || "",
                email: res.data.email || "",
                password: ""
            });


            setMessage(
                "Profile updated successfully."
            );

            setMessageType("success");


        } catch (error) {

            console.error(error);

            setMessage(
                error.response?.data?.message ||
                "Failed to update profile."
            );

            setMessageType("error");

        } finally {

            setSaving(false);

        }
    };


    // =========================
    // Loading
    // =========================

    if (loading) {

        return (
            <div className="profile-loading">
                Loading...
            </div>
        );

    }


    // =========================
    // UI
    // =========================

    return (

        <div className="profile-page">

            <div className="profile-header">

                <div>

                    <h1>
                        My Profile
                    </h1>

                    <p>
                        Update your account information
                    </p>

                </div>

            </div>


            <div className="profile-card">


                {/* Avatar */}

                <div className="profile-avatar">

                    {form.username
                        ? form.username
                            .charAt(0)
                            .toUpperCase()
                        : "O"
                    }

                </div>


                <form onSubmit={handleSubmit}>


                    {/* Username */}

                    <div className="form-group">

                        <label>
                            Username
                        </label>

                        <input
                            type="text"
                            name="username"
                            value={form.username}
                            onChange={handleChange}
                            required
                        />

                    </div>


                    {/* Email */}

                    <div className="form-group">

                        <label>
                            Email
                        </label>

                        <input
                            type="email"
                            name="email"
                            value={form.email}
                            onChange={handleChange}
                            required
                        />

                    </div>


                    {/* Password */}

                    <div className="form-group">

                        <label>
                            New Password
                        </label>


                        <div className="password-wrapper">

                            <input
                                type={
                                    showPassword
                                        ? "text"
                                        : "password"
                                }
                                name="password"
                                value={form.password}
                                onChange={handleChange}
                                placeholder="Leave blank to keep current password"
                                autoComplete="new-password"
                            />


                            <button
                                type="button"
                                onClick={() =>
                                    setShowPassword(
                                        prev => !prev
                                    )
                                }
                            >

                                {showPassword
                                    ? "Hide"
                                    : "Show"
                                }

                            </button>

                        </div>

                    </div>


                    {/* Message */}

                    {message && (

                        <div
                            className={`profile-message ${messageType}`}
                        >
                            {message}
                        </div>

                    )}


                    {/* Submit */}

                    <button
                        type="submit"
                        disabled={saving}
                    >

                        {saving
                            ? "Updating..."
                            : "Update Profile"
                        }

                    </button>

                </form>

            </div>

        </div>

    );
}

export default OwnerProfile;