import { BrowserRouter, Route, Routes } from "react-router-dom";
import Test from "../components/test";
import SignupPage from "../SignupPage.jsx";

const App = () => {
  return (
    // <BrowserRouter>
      // <Routes>
        // <Route path="/" element={<Test />}></Route>
      // </Routes>
    // </BrowserRouter>
    <SignupPage />
  );
};

export default App;
