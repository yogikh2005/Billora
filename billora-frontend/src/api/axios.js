import axios from "axios";

const api = axios.create({
    baseURL: "https://billora-backend-fecjdde8b3ege0e4.westus3-01.azurewebsites.net"
});

api.interceptors.request.use((config) => {

    const token = sessionStorage.getItem("token");

    // Don't send JWT for public endpoints
    if (
        token &&
        config.url !== "/auth/signin" &&
        config.url !== "/auth/signup"
    ) {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
});

export default api;