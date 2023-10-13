import axios from "axios";
import { useEffect, useState } from "react";
import { styled } from "styled-components";
import { usePagination } from "../hooks/usePagination";

type Props = {
  id: string | undefined;
};

type Comment = {
  commentId: number;
  name: string;
  content: string;
  createdAt: string;
  modifiedAt: string;
};

const Comment = ({ id }: Props): JSX.Element => {
  const [commentData, setCommentData] = useState<(Comment | null)[]>([]);
  const [commentNum, setCommentNum] = useState<number>(0);
  const {
    currentPage,
    totalPages,
    setTotalPages,
    onPageChangeHandler,
    onPrevPageHandler,
    onNextPageHandler,
  } = usePagination();

  useEffect(() => {
    axios
      .get(
        `http://ec2-43-202-120-133.ap-northeast-2.compute.amazonaws.com:8080/comment/diary/${id}?page=${currentPage}`,
      )
      .then((res) => {
        setCommentData(res.data.data);
        setCommentNum(res.data.data.length);
      })
      .catch((err) => {
        console.log(err);
      });
  }, []);

  return (
    <CommentCon>
      <CommentTitle>댓글 {commentNum}개</CommentTitle>
      <CommentArea>
        {commentData.map((el: Comment | null) => {
          return (
            <CommentSection key={el?.commentId}>
              <CommentTop>
                <CommentAuthor>{el?.name}</CommentAuthor>
                <CommentTime>{el?.createdAt.slice(0, 10).replace(/-/g, "/")}</CommentTime>
              </CommentTop>
              <CommentContent>{el?.content}</CommentContent>
            </CommentSection>
          );
        })}
      </CommentArea>
      <CommentForm>
        <textarea placeholder="댓글을 입력해주세요."></textarea>
        <button>댓글 작성</button>
      </CommentForm>
    </CommentCon>
  );
};

export default Comment;

const CommentCon = styled.div`
  max-width: 1200px;
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
  align-items: flex-end;
  border-top: 1px solid #eaeaea;
  padding-top: 20px;

  textarea {
    width: 100%;
    height: 100px;
    padding: 15px 18px;
    box-shadow: 0 0 0 1px #bebebe inset;
    border-radius: 4px;
    box-sizing: border-box;
    resize: none;
  }

  button {
    padding: 10px 30px;
    color: #ffffff;
    font-size: 16px;
    background-color: #1a298e;
    border-radius: 4px;
    margin-top: 18px;
  }
`;

const CommentArea = styled.ul`
  width: 100%;
  margin-top: 20px;
`;

const CommentSection = styled.li`
  border-top: 1px solid #eaeaea;
  padding: 20px 0;
`;

const CommentTop = styled.div`
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
`;

const CommentAuthor = styled.span`
  display: block;
  font-weight: 700;
  margin-bottom: 5px;
`;

const CommentTime = styled.span`
  color: #616161;
  font-size: 12px;
  font-weight: 400;
`;

const CommentContent = styled.div`
  max-width: 1200px;
`;
