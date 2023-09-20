import styled from "styled-components";
import WriteForm from "../components/WriteForm";

const WriteCon = styled.main`
  position: relative;
  min-height: calc(100vh - 120px);
`;

const WriteEdit = () => {
  return (
    <WriteCon>
      <WriteForm></WriteForm>
    </WriteCon>
  );
};

export default WriteEdit;
