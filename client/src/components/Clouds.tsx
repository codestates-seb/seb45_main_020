import styled from "styled-components";
import Lightning from "../assets/images/lightning.png";

const Clouds = () => {
  return (
    <CloudBox>
      <div className="x1">
        <div className="cloud"></div>
      </div>

      <div className="x2">
        <div className="cloud"></div>
      </div>

      <div className="x3">
        <div className="cloud"></div>
      </div>
    </CloudBox>
  );
};
export default Clouds;

const CloudBox = styled.div`
  bottom: 0;
  left: 0;
  padding-top: 50px;
  position: absolute;
  right: 0;
  top: 0;
  z-index: 1;
  overflow: hidden;

  @keyframes animateCloud {
    0% {
      margin-left: -500px;
    }
    100% {
      margin-left: 100%;
    }
  }
  .x1 {
    animation: animateCloud 35s linear infinite;
    transform: scale(0.5);
    position: relative;
    &::before {
      content: "";
      position: absolute;
      left: 143px;
      bottom: -130px;
      width: 120px;
      height: 120px;
      background: url(${Lightning}) no-repeat center / contain;
      transform: rotate(19deg);
    }
    &::after {
      content: "";
      position: absolute;
      left: 100px;
      bottom: -100px;
      width: 81px;
      height: 81px;
      background: url(${Lightning}) no-repeat center / contain;
      transform: rotate(19deg);
    }
  }
  .x2 {
    animation: animateCloud 20s linear infinite;
    transform: scale(0.25);
  }
  .x3 {
    animation: animateCloud 30s linear infinite;
    transform: scale(0.35);
  }

  .cloud {
    background: linear-gradient(to top, #aaa 5%, #e5e5e5 100%);
    border-radius: 100px;
    box-shadow: 0 8px 5px rgba(0, 0, 0, 0.1);
    height: 120px;
    position: relative;
    width: 350px;
  }

  .cloud:after,
  .cloud:before {
    background: #e5e5e5;
    content: "";
    position: absolute;
    z-index: -1;
  }

  .cloud:after {
    border-radius: 100px;
    height: 100px;
    left: 50px;
    top: -50px;
    width: 100px;
  }

  .cloud:before {
    border-radius: 200px;
    width: 180px;
    height: 180px;
    right: 50px;
    top: -90px;
  }
`;
