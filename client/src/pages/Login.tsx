import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import axios from "axios";
import { Cookies } from "react-cookie";
import styled, { keyframes } from "styled-components";
import Wave from "../components/common/Wave";
import ImgSun from "../assets/images/img_sun.png";
import ImgCharacter from "../assets/images/character.png";
import loading from "../assets/images/loading.gif";
import Github from "../assets/images/github.png";
import Kakao from "../assets/images/kakao.png";

const Login = (): JSX.Element => {
  const [isHidePassword, setIsHidePassword] = useState<boolean>(true);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const navigate = useNavigate();
  const cookies = new Cookies();

  interface Form {
    email: string;
    password: string;
  }

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<Form>();

  function handleLoginSubmit(data: Form): void {
    setIsLoading(true);
    axios
      .post(
        `http://ec2-43-202-120-133.ap-northeast-2.compute.amazonaws.com:8080/auth/login`,
        {
          email: data.email,
          password: data.password,
        },
        { headers: { "Content-Type": "application/json" } },
      )
      .then((res) => {
        const accessToken = res.headers.authorization;
        cookies.set("is_login", `${accessToken}`);
        localStorage.setItem("token", accessToken);
        localStorage.setItem("memberId", res.data.memberId);
        localStorage.setItem("email", data.email);

        setIsLoading(false);
        navigate("/");
      })
      .catch((err) => {
        alert(err.response.data.message);
        setIsLoading(false);
      });
  }

  function handleKakaoLogin(): void {
    const REST_API_KEY = process.env.REACT_APP_REST_API_KEY_K;
    const REDIRECT_URI = process.env.REACT_APP_REDIRECT_URI_K;

    const link = `https://kauth.kakao.com/oauth/authorize?client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}&response_type=code`;

    window.location.href = link;
  }

  function handleGithubLogin(): void {
    const REST_API_KEY = process.env.REACT_APP_REST_API_KEY_G;

    const link = `https://github.com/login/oauth/authorize?client_id=${REST_API_KEY}&scope=user:email`;

    window.location.href = link;
  }

  return (
    <MainCon>
      <Wave />
      <Sun>
        <img src={ImgSun} alt="" />
      </Sun>
      <Character>
        <img src={ImgCharacter} alt="" />
      </Character>
      <LoginCon onSubmit={handleSubmit(handleLoginSubmit)}>
        <LoginTitle>로그인</LoginTitle>
        <LoginInputCon>
          <span>이메일</span>
          <input
            type="text"
            {...register("email", {
              required: "이메일을 입력해주세요.",
              pattern: { value: /\S+@\S+\.\S+/, message: "올바른 이메일 주소를 입력해주세요." },
            })}
          />
          {errors?.email ? <LoginWarning>{errors.email.message}</LoginWarning> : null}
        </LoginInputCon>
        <LoginInputCon>
          <span>비밀번호</span>
          <LoginInputPasswordCon>
            <input
              type={isHidePassword ? "password" : "text"}
              {...register("password", {
                required: "비밀번호를 입력해주세요.",
              })}
            ></input>
            {isHidePassword ? (
              <svg
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                className="passwordShow"
                onClick={() => setIsHidePassword(false)}
              >
                <path
                  d="M12 5C15.679 5 20.162 7.417 21.73 10.901C21.876 11.229 22 11.611 22 12C22 12.388 21.877 12.771 21.73 13.099C20.161 16.583 15.678 19 12 19C8.321 19 3.838 16.583 2.27 13.099C2.124 12.77 2 12.389 2 12C2 11.612 2.123 11.229 2.27 10.901C3.839 7.417 8.322 5 12 5ZM12 8C10.9391 8 9.92172 8.42143 9.17157 9.17157C8.42143 9.92172 8 10.9391 8 12C8 13.0609 8.42143 14.0783 9.17157 14.8284C9.92172 15.5786 10.9391 16 12 16C13.0609 16 14.0783 15.5786 14.8284 14.8284C15.5786 14.0783 16 13.0609 16 12C16 10.9391 15.5786 9.92172 14.8284 9.17157C14.0783 8.42143 13.0609 8 12 8ZM12 10C12.5304 10 13.0391 10.2107 13.4142 10.5858C13.7893 10.9609 14 11.4696 14 12C14 12.5304 13.7893 13.0391 13.4142 13.4142C13.0391 13.7893 12.5304 14 12 14C11.4696 14 10.9609 13.7893 10.5858 13.4142C10.2107 13.0391 10 12.5304 10 12C10 11.4696 10.2107 10.9609 10.5858 10.5858C10.9609 10.2107 11.4696 10 12 10Z"
                  fill="black"
                />
              </svg>
            ) : (
              <svg
                width="24"
                height="24"
                viewBox="0 0 24 24"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                className="passwordHide"
                onClick={() => setIsHidePassword(true)}
              >
                <path
                  d="M6.99951 6.362C8.51195 5.46328 10.2402 4.99251 11.9995 5C18.3065 5 21.3665 10.683 21.9095 11.808C21.9695 11.931 21.9695 12.069 21.9095 12.193C21.5575 12.921 20.1535 15.555 17.4995 17.324M13.9995 18.8C13.3413 18.9341 12.6712 19.0012 11.9995 19C5.69251 19 2.63251 13.317 2.08951 12.192C2.06017 12.1319 2.04492 12.0659 2.04492 11.999C2.04492 11.9321 2.06017 11.8661 2.08951 11.806C2.30851 11.354 2.92951 10.174 3.99951 8.921M9.99951 9.764C10.571 9.2531 11.3165 8.98037 12.0828 9.00182C12.8491 9.02326 13.5781 9.33725 14.1202 9.87932C14.6623 10.4214 14.9762 11.1504 14.9977 11.9167C15.0191 12.683 14.7464 13.4285 14.2355 14M2.99951 3L20.9995 21"
                  stroke="black"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                />
              </svg>
            )}
          </LoginInputPasswordCon>
          {errors?.password ? <LoginWarning>{errors.password.message}</LoginWarning> : null}
        </LoginInputCon>
        <LoginLinkCon>
          <Link to="/signup">
            <span>회원가입</span>
          </Link>
          <span>|</span>
          <Link to="/">
            <span>비밀번호 찾기</span>
          </Link>
        </LoginLinkCon>
        {isLoading ? <LoadingImg src={loading} /> : <LoginBtn>로그인</LoginBtn>}
        <LoginLine
          width="402"
          height="24"
          viewBox="0 0 402 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M201.824 11.9091C201.824 12.983 201.63 13.911 201.242 14.6932C200.854 15.4754 200.322 16.0786 199.646 16.5028C198.97 16.9271 198.198 17.1392 197.33 17.1392C196.461 17.1392 195.689 16.9271 195.013 16.5028C194.337 16.0786 193.805 15.4754 193.417 14.6932C193.029 13.911 192.835 12.983 192.835 11.9091C192.835 10.8352 193.029 9.9072 193.417 9.125C193.805 8.3428 194.337 7.73958 195.013 7.31534C195.689 6.8911 196.461 6.67898 197.33 6.67898C198.198 6.67898 198.97 6.8911 199.646 7.31534C200.322 7.73958 200.854 8.3428 201.242 9.125C201.63 9.9072 201.824 10.8352 201.824 11.9091ZM200.631 11.9091C200.631 11.0275 200.483 10.2834 200.188 9.67685C199.897 9.07031 199.5 8.61127 199 8.29972C198.503 7.98816 197.946 7.83239 197.33 7.83239C196.713 7.83239 196.155 7.98816 195.654 8.29972C195.157 8.61127 194.761 9.07031 194.466 9.67685C194.174 10.2834 194.028 11.0275 194.028 11.9091C194.028 12.7907 194.174 13.5348 194.466 14.1413C194.761 14.7479 195.157 15.2069 195.654 15.5185C196.155 15.83 196.713 15.9858 197.33 15.9858C197.946 15.9858 198.503 15.83 199 15.5185C199.5 15.2069 199.897 14.7479 200.188 14.1413C200.483 13.5348 200.631 12.7907 200.631 11.9091ZM203.897 17V6.81818H207.337C208.133 6.81818 208.786 6.95407 209.296 7.22585C209.807 7.49432 210.184 7.86387 210.43 8.33452C210.675 8.80516 210.798 9.34044 210.798 9.94034C210.798 10.5402 210.675 11.0722 210.43 11.5362C210.184 12.0002 209.808 12.3648 209.301 12.63C208.794 12.8918 208.146 13.0227 207.357 13.0227H204.573V11.9091H207.317C207.861 11.9091 208.299 11.8295 208.63 11.6705C208.965 11.5114 209.207 11.286 209.356 10.9943C209.508 10.6993 209.585 10.348 209.585 9.94034C209.585 9.53267 209.508 9.17637 209.356 8.87145C209.203 8.56652 208.96 8.3312 208.625 8.16548C208.29 7.99645 207.848 7.91193 207.298 7.91193H205.13V17H203.897ZM208.69 12.4261L211.195 17H209.763L207.298 12.4261H208.69Z"
            fill="#BEBEBE"
          />
          <line y1="12.5" x2="188" y2="12.5" stroke="#BEBEBE" />
          <line x1="216" y1="12.5" x2="402" y2="12.5" stroke="#BEBEBE" />
        </LoginLine>
        <LoginSocialCon>
          <LoginSocial>
            <img src={Github} onClick={handleGithubLogin} />
          </LoginSocial>
          <LoginSocial onClick={handleKakaoLogin}>
            <img src={Kakao} />
          </LoginSocial>
        </LoginSocialCon>
      </LoginCon>
    </MainCon>
  );
};

export default Login;

const MainCon = styled.main`
  position: relative;
  min-height: calc(100vh - 120px);
`;

const Gelatine = keyframes`
  from, to { transform: scale(1, 1); }
  25% { transform: scale(0.9, 1.1); }
  50% { transform: scale(1.1, 0.9); }
  75% { transform: scale(0.95, 1.05); }
`;

const Sun = styled.div`
  position: absolute;
  top: 7%;
  left: 10%;
  z-index: -1;
  animation: ${Gelatine} 1s infinite;
  > img {
    width: 6.5vw;
    min-width: 80px;
    max-width: 120px;
  }
`;

const Bounce = keyframes`
  0%, 20%, 50%, 80%, 100% {transform: translateY(0);}
	40% {transform: translateY(-30px);}
	60% {transform: translateY(-15px);}
`;

const Character = styled.div`
  position: absolute;
  top: 45%;
  left: 15%;
  z-index: -1;
  animation: ${Bounce} 2s infinite;
  > img {
    width: 15vw;
    min-width: 100px;
    max-width: 170px;
  }
`;

const LoginCon = styled.form`
  min-height: calc(100vh - 120px);
  width: 550px;
  background-color: rgba(255, 255, 255, 0.8);
  margin-left: auto;
  margin-right: 227px;
  box-shadow: 0 4px 20px rgba(163, 163, 163, 0.25);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
  @media screen and (max-width: 1200px) {
    margin-right: 20px;
  }
`;

const LoginTitle = styled.span`
  font-size: 30px;
  display: block;
  margin-bottom: 10px;
`;

const LoginInputCon = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
  margin-top: 20px;
  gap: 10px;

  input {
    width: 400px;
    height: 39px;
    padding: 10px 13px;
    box-shadow: 0 0 0 1px #bebebe inset;
    border-radius: 4px;
    box-sizing: border-box;
  }
`;

const LoginWarning = styled.span`
  font-size: 12px;
  color: #e23636;
`;

const LoginInputPasswordCon = styled.div`
  width: 400px;
  height: 39px;
  position: relative;

  input {
    width: 400px;
    padding: 10px 13px;
    box-shadow: 0 0 0 1px #bebebe inset;
    border-radius: 4px;
  }

  svg {
    position: absolute;
    top: 50%;
    right: 12px;
  }

  .passwordShow {
    width: 24px;
    height: 24px;
    transform: translate(0%, -50%);
  }

  .passwordHide {
    width: 22px;
    height: 21px;
    transform: translate(-4%, -50%);
  }
`;

const LoginLinkCon = styled.div`
  margin-top: 12px;
  display: flex;
  align-items: center;
  gap: 5px;

  span:nth-child(2) {
    color: #bebebe;
  }
`;

const LoadingImg = styled.img`
  width: 50px;
  height: 50px;
  margin-top: 70px;
`;

const LoginBtn = styled.button`
  width: 400px;
  height: 50px;
  background-color: #1a298e;
  border-radius: 4px;
  font-size: 20px;
  margin-top: 70px;
`;

const LoginLine = styled.svg`
  width: 100%;
  margin-top: 30px;
`;

const LoginSocialCon = styled.div`
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 12px;
  gap: 10px;
`;

const LoginSocial = styled.div`
  width: 46px;
  height: 46px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #ffffff;
  border-radius: 4px;
  box-shadow: 0 0 0 1px #bebebe inset;

  &:hover {
    cursor: pointer;
  }

  img {
    width: 24px;
    height: 24px;
  }
`;
