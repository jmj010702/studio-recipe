import { useState } from "react";
import api from "../api/axios";

const Test = () => {
    const[count, setCount] = useState(0);
    const[data, setData] = useState(null);
    const[isResponse, SetIsResponse] = useState(false);

    const plus = () => {
        setCount(prev => prev + 1);
    }

    const sendToCount = async () => {
        await api.get(`/test/${count}`)
            .then(response => {
                if(response.status === 200){
                    setData(response.data);
                    //간단 예시를 위해  조건부 렌더링에 사용한 거
                    SetIsResponse(true);
                }
            })
                .catch(error => {
                    console.log("error", error);
                });
    }

    return (
        <div>
        <input 
        type="number"
        value={count}
        // onChange={(e) => setCount(Number(e.target.value))}
        required
        readOnly
        />
        <button onClick={() => plus()}>카운트 증가</button>
        <button onClick={() => setCount(prev => prev - 1)}>카운트 감소</button>
        <button onClick={() => sendToCount()}>서버에 보내기</button>
        {isResponse && data}

        </div>
    )
}

export default Test;