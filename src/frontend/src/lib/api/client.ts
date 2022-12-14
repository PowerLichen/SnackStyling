import axios from "axios";
import address from "./address";
// axios 요청 클라이언트 생성
const client = axios.create();

client.interceptors.request.use((config) => {
  const token =
    config.url?.split("/").pop() === "token"
      ? localStorage.getItem("refreshToken")
      : localStorage.getItem("accessToken");
  if (config.headers) config.headers.Authorization = token ? `${token}` : "";
  return config;
});

// accessToken expire시에 리프레시 토큰으로 새로운 엑세스 토큰 받기
client.interceptors.response.use(
  (response) => {
    // 요청 성공 시 특정 작업 수행
    return response;
  },
  async (error) => {
    // 요청 실패 시 특정 작업 수행
    if (error.response.status === 426) {
      //access token expire시 재발급하고 다시 요청
      client.defaults.headers.common["Authorization"] = `${localStorage.getItem(
        "refreshToken"
      )}`;
      const res = await client.post(address.api + `/accounts/token`);

      localStorage.setItem("accessToken", res.data.accessToken);

      return client.request(error.config);
    }

    // refresh 토큰도 만료 된 경우 status 500
    if (
      error.response.status === 500 ||
      error.response.status === 401 ||
      error.response.status === 403
    ) {
      localStorage.removeItem("accessToken");
      localStorage.removeItem("removeToken");
      localStorage.removeItem("id");

      window.location.href = "/home";
      return Promise.reject(error);
    }

    return Promise.resolve(error.response);
  }
);

export default client;
