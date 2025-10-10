import axios from "axios";

const url = import.meta.env.VITE_APP_SEVER_URL;

console.info("url: ", url);

const api = axios.create({
    baseURL : url,
    withCredentials : true
});

export default api;