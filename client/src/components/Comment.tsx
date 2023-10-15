import axios from "axios";
import { useEffect, useState } from "react";
import { styled } from "styled-components";
import { usePagination } from "../hooks/usePagination";
import { getAccessToken } from "../util/auth";
import edit from "../assets/images/edit-white.png";
import trash from "../assets/images/trash.png";

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

type EditComment = {
  state: boolean;
  commentId: number | undefined | null;
};

const Comment = ({ id }: Props): JSX.Element => {
  const [commentData, setCommentData] = useState<(Comment | null)[]>([]);
  const [commentNum, setCommentNum] = useState<number>(0);
  const [commentTextarea, setCommentTextarea] = useState<string>("");
  const [isEdit, setIsEdit] = useState<EditComment>({ state: false, commentId: null });
  const [editComment, setEditComment] = useState<string | undefined>("");
  const {
    currentPage,
    // totalPages,
    // setTotalPages,
    // onPageChangeHandler,
    // onPrevPageHandler,
    // onNextPageHandler,
  } = usePagination();

  type Headers = Record<string, string>;
  const headers: Headers = {};
  const token = getAccessToken();
  if (token) {
    headers["Authorization"] = `${token}`;
  }

  function getCommentData(): void {
    axios
      .get(
        `http://ec2-43-202-120-133.ap-northeast-2.compute.amazonaws.com:8080/comment/diary/${id}?page=${currentPage}`,
      )
      .then((res) => {
        setCommentData(res.data.data.reverse());
        setCommentNum(res.data.data.length);
      })
      .catch((err) => {
        console.log(err);
      });
  }

  useEffect(() => {
    getCommentData();
  }, []);

  function handleCommentSubmit(e: React.FormEvent<HTMLFormElement>): void {
    e.preventDefault();
    const formData = Object.fromEntries(new FormData(e.currentTarget));

    if (formData.content === "") {
      alert("댓글의 내용을 입력해주세요.");
      return;
    }

    axios
      .post(`http://ec2-43-202-120-133.ap-northeast-2.compute.amazonaws.com:8080/comment`, {
        diaryId: id,
        memberId: localStorage.getItem("memberId"),
        content: formData.content,
      })
      .then(() => {
        getCommentData();
        setCommentTextarea("");
      })
      .catch((err) => {
        console.log(err);
      });
  }

  function handleCommentEdit(id: number | undefined, content: string | undefined): void {
    setIsEdit({ state: true, commentId: id });
    setEditComment(content);
  }

  function cancelCommentEdit(): void {
    setIsEdit({ state: false, commentId: null });
    setEditComment("");
  }

  function handleEditComment(id: number | undefined): void {
    axios
      .patch(
        `http://ec2-43-202-120-133.ap-northeast-2.compute.amazonaws.com:8080/comment/${id}`,
        { content: editComment },
        {
          headers,
        },
      )
      .then(() => {
        cancelCommentEdit();
        getCommentData();
      })
      .catch((err) => console.log(err));
  }

  function handleCommentDelete(id: number | undefined): void {
    if (window.confirm("댓글을 삭제하시겠습니까?")) {
      axios
        .delete(
          `http://ec2-43-202-120-133.ap-northeast-2.compute.amazonaws.com:8080/comment/${id}`,
          {
            headers,
          },
        )
        .then(() => {
          getCommentData();
        })
        .catch((err) => console.log(err));
    }
  }

  return (
    <CommentCon>
      <CommentTitle>댓글 {commentNum}개</CommentTitle>
      <CommentArea>
        {commentData.map((el: Comment | null) => {
          return (
            <CommentSection key={el?.commentId}>
              {isEdit.state && isEdit.commentId === el?.commentId ? (
                <>
                  <CommentEditInput
                    value={editComment}
                    onChange={(e) => setEditComment(e.target.value)}
                  ></CommentEditInput>
                  <CommentEditBtnCon>
                    <CommentEditBtn className="comment_cancel" onClick={cancelCommentEdit}>
                      취소
                    </CommentEditBtn>
                    <CommentEditBtn
                      className="comment_edit"
                      onClick={() => handleEditComment(el?.commentId)}
                    >
                      수정
                    </CommentEditBtn>
                  </CommentEditBtnCon>
                </>
              ) : (
                <>
                  <CommentTop>
                    <CommentInfo>
                      <CommentAuthor>{el?.name}</CommentAuthor>
                      <CommentTime>{el?.createdAt.slice(0, 10).replace(/-/g, "/")}</CommentTime>
                    </CommentInfo>
                    <CommentBtnCon>
                      <CommentBtn
                        className="comment_edit"
                        onClick={() => handleCommentEdit(el?.commentId, el?.content)}
                      >
                        <img src={edit} className="comment_edit_img" />
                      </CommentBtn>
                      <CommentBtn
                        className="comment_delete"
                        onClick={() => handleCommentDelete(el?.commentId)}
                      >
                        <img src={trash} className="comment_delete_img" />
                      </CommentBtn>
                    </CommentBtnCon>
                  </CommentTop>
                  <CommentContent>{el?.content}</CommentContent>
                </>
              )}
            </CommentSection>
          );
        })}
      </CommentArea>
      <CommentForm onSubmit={(e) => handleCommentSubmit(e)}>
        <textarea
          placeholder="댓글을 입력해주세요."
          name="content"
          value={commentTextarea}
          onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) =>
            setCommentTextarea(e.target.value)
          }
        ></textarea>
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
    cursor: pointer;
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

const CommentEditInput = styled.textarea`
  width: 100%;
  height: 100px;
  padding: 15px 18px;
  box-shadow: 0 0 0 1px #bebebe inset;
  border-radius: 4px;
  box-sizing: border-box;
  resize: none;
`;

const CommentTop = styled.div`
  width: 100%;
  display: flex;
  justify-content: space-between;
`;

const CommentInfo = styled.div`
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

const CommentBtnCon = styled.div`
  display: flex;
  gap: 5px;

  .comment_edit {
    background-color: #ffc03f;
  }

  .comment_delete {
    background-color: #f36c68;
  }

  .comment_cancel {
    background-color: #bebebe;
  }
`;

const CommentEditBtnCon = styled(CommentBtnCon)`
  width: 100%;
  justify-content: flex-end;
`;

const CommentBtn = styled.div`
  width: 30px;
  height: 30px;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;

  .comment_edit_img {
    width: 18px;
    height: 19px;
  }

  .comment_delete_img {
    width: 25px;
    height: 25px;
  }
`;

const CommentEditBtn = styled(CommentBtn)`
  width: 80px;
  margin-top: 10px;
  color: #ffffff;
  font-weight: 600;
`;

const CommentContent = styled.div`
  max-width: 1200px;
`;
