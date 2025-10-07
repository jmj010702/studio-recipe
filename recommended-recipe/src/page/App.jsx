import { BrowserRouter, Route, Routes } from "react-router-dom";
import Test from "../components/test";

const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Test />}></Route>
      </Routes>
    </BrowserRouter>
  );
};

export default App;
