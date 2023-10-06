import { useRecoilValue, useSetRecoilState } from "recoil";
import styled from "styled-components";
import { consentModalState, teamModalState } from "../../atoms/atoms";

interface ModalProp {
  children: JSX.Element;
}

const StyledModal = styled.div`
  z-index: 999;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;

  section {
    position: relative;
    background: #fff;
    padding: 24px 30px;
    box-shadow:
      0 1px 4px hsla(0, 0%, 0%, 0.09),
      0 3px 8px hsla(0, 0%, 0%, 0.09),
      0 4px 13px hsla(0, 0%, 0%, 0.13);
    border-radius: 8px;
    max-width: 620px;

    .close-button {
      position: absolute;
      padding: 16px;
      top: 10px;
      right: 7px;
      cursor: pointer;
    }
  }

  // 약관 동의 모달 셋팅
  .consent-con {
    > strong {
      display: block;
      padding-bottom: 12px;
      margin-bottom: 24px;
      font-size: 24px;
      border-bottom: 1px solid #e7e7e7;
    }
  }

  .txt-conbox {
    height: 500px;
    overflow: auto;
    padding-right: 10px;
  }

  .txt-conbox::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }
  .txt-conbox::-webkit-scrollbar-thumb {
    background-color: #d6d6d6;
    border-radius: 100px;
  }
  .txt-conbox::-webkit-scrollbar-track {
    border-radius: 100px;
    background-clip: padding-box;
    border: 1px solid transparent;
    background-color: #efefef;
  }

  .consent-intro {
    margin-bottom: 5px;
    line-height: 24px;
  }

  .consent-link {
    text-decoration: underline;
    cursor: pointer;
  }

  .consent-h1 {
    margin: 30px 0 20px 0;
    font-size: 20px;
    font-weight: 600;
  }

  .consent-h2 {
    margin-bottom: 10px;
    font-weight: 500;
  }

  .consent-h3 {
    margin-bottom: 20px;
    font-size: 14px;
    line-height: 1.5;
  }

  li {
    text-indent: -18px;
    margin-left: 18px;
  }

  .consent-indent {
    margin: 2px 0 2px 37px;
  }

  // 팀원 소개 모달 셋팅
  .footer-con {
    margin: 20px 50px;
    color: #444444;
    text-align: center;
  }

  .footer-h1 {
    font-size: 28px;
    font-weight: 600;
    letter-spacing: 2px;
    color: #4270cf;
  }

  .footer-intro {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
  }

  .footer-h2 {
    font-size: 22px;
    font-size: 500;
    letter-spacing: 1px;
  }

  .footer-team-con {
    display: flex;
    align-items: flex-start;
    gap: 50px;
    margin-top: 20px;
  }

  .footer-team {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }

  .footer-teammate {
    width: 120px;
    height: 120px;
    margin-bottom: 10px;
    border-radius: 50%;
    border: 0.5px solid rgba(68, 68, 68, 0.4);
  }

  .footer-github {
    margin-top: 5px;
    cursor: pointer;
  }
`;

const Modal = ({ children }: ModalProp): JSX.Element => {
  const setConsentModal = useSetRecoilState(consentModalState);
  const setTeamModal = useSetRecoilState(teamModalState);
  const consentModal = useRecoilValue(consentModalState);
  const teamModal = useRecoilValue(teamModalState);

  const closeModal = () => {
    if (consentModal) {
      setConsentModal(false);
    } else if (teamModal) {
      setTeamModal(false);
    }
  };

  return (
    <StyledModal onClick={closeModal}>
      <section className="modal">
        <div className="close-button" onClick={closeModal}>
          <svg
            aria-hidden="true"
            className="svg-icon iconClearSm"
            width="24"
            height="24"
            viewBox="0 0 14 14"
          >
            <path d="M12 3.41 10.59 2 7 5.59 3.41 2 2 3.41 5.59 7 2 10.59 3.41 12 7 8.41 10.59 12 12 10.59 8.41 7 12 3.41Z"></path>
          </svg>
        </div>
        {children}
      </section>
    </StyledModal>
  );
};

export default Modal;
export {};
