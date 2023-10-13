import { styled } from "styled-components";

const Comment = () => {
  return (
    <CommentCon>
      <CommentTitle>댓글</CommentTitle>
      <CommentForm>
        <input type="text"></input>
        <textarea></textarea>
      </CommentForm>
    </CommentCon>
  );
};

export default Comment;

const CommentCon = styled.div`
  width: 1200px;
  margin: 30px auto 0;
  padding: 24px 40px;
  background-color: rgba(255, 255, 255, 0.7);
  box-shadow: 2px 4px 6px rgba(185, 185, 185, 0.25);
  border: 1px solid #eaeaea;
  border-radius: 12px;
`;

const CommentTitle = styled.div`
  font-size: 22px;
  font-weight: 600;
  padding-left: 5px;
  margin-bottom: 16px;
`;

const CommentForm = styled.form`
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  border-top: 1px solid #eaeaea;
  padding-top: 16px;

  input {
    border: 1px solid #eaeaea;
  }

  textarea {
    border: 1px solid #eaeaea;
  }
`;
