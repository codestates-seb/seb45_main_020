import axios from "axios";
import styled from "styled-components";
import { useNavigate, useParams } from "react-router-dom";
import { useFetch } from "../hooks/useFetch";
import { diaryDetailState } from "../atoms/atoms";
import { DiaryDetail } from "../types/types";
import { getFormattedDate } from "../util/date";
import { getAccessToken, getEmail, getUserId } from "../util/auth";
import backArrow from "../assets/images/back-arrow.png";
import thumbUp from "../assets/images/thumb-up-icon.png";
import eye from "../assets/images/eye-icon.png";
import thumbUpWhite from "../assets/images/thumb-up-icon-white.png";
import Loading from "../components/common/Loading";
import SubWave from "../components/common/SubWave";
import Comment from "../components/Comment";

const Detail = (): JSX.Element => {
  const navigate = useNavigate();
  const params = useParams();
  const { fetchData, isLoading, isError, data } = useFetch<DiaryDetail>(
    diaryDetailState,
    `diary/${params.id}`,
  );
  const patchBtnHandler = () => {
    // "수정" 버튼을 클릭할 때 글쓰기/편집 페이지로 이동하도록 합니다.
    // diaryId는 수정할 일기의 ID입니다.-
    navigate(`/write-edit/${params.id}`); // 경로 및 파라미터를 적절히 수정하십시오.
  };
  type Headers = Record<string, string>;
  const headers: Headers = {};
  const token = getAccessToken();
  if (token) {
    headers["Authorization"] = `${token}`;
  }
  const likeBtnHandler = async () => {
    try {
      const response = await axios.post(`recommend?memberId=${getUserId()}&diaryId=${params.id}`, {
        headers,
      });
      if (response.status === 201 || 200) {
      } else {
        console.error(`${response.status} 실패`);
      }
    } catch (error) {
      alert("권한이 없습니다.");
    }
    await fetchData();
  };
  const deleteBtnHandler = async () => {
    try {
      const response = await axios.delete(`diary/${params.id}`, {
        headers,
      });
      if (response.status === 201 || 200) {
      }
    } catch (error) {
      alert("권한이 없습니다.");
    }
    await navigate(-1);
  };
  const scrapBtnHandler = async () => {
    try {
      const response = await axios.post(
        "scrap",
        {
          memberId: getUserId(),
          diaryId: data.data.diaryId,
        },
        {
          headers,
        },
      );
      if (response.status === 201 || 200) {
      }
    } catch (error) {
      alert("권한이 없습니다.");
    }
    await fetchData();
  };
  if (isError) {
    navigate("/diary/error");
  }
  return (
    <>
      {isLoading ? <Loading /> : null}
      <DetailBg>
        <SubWave />
        <DetailContentContainer>
          <DetailMainContent>
            <DetailTitleContainer>
              <DetailArrowContainer>
                <img src={backArrow} onClick={() => navigate(-1)} />
              </DetailArrowContainer>
              <DetailTitle>{data?.data.title}</DetailTitle>
            </DetailTitleContainer>
            <DetailReaderRecordContainer>
              <DetailSide>
                <DetailDiaryInfo>{data?.data.name}</DetailDiaryInfo>
                <DetailDiaryInfo>
                  {getFormattedDate(
                    data?.data.modifiedAt ? data?.data.modifiedAt : data?.data.createdAt,
                  )}
                </DetailDiaryInfo>
              </DetailSide>
              <DetailReaderRecord>
                <img src={thumbUp} />
                <div>{data?.data.recommendedCount}</div>
              </DetailReaderRecord>
              <DetailReaderRecord>
                <img src={eye} />
                <div>{data?.data.views}</div>
              </DetailReaderRecord>
            </DetailReaderRecordContainer>
            <DetailConentParagraph>
              <div dangerouslySetInnerHTML={{ __html: data?.data.content }}></div>
            </DetailConentParagraph>
            <DetailBtnContainer>
              {data?.data.recommend ? (
                <DetailLikeBtnFocus
                  onClick={() => {
                    likeBtnHandler();
                  }}
                >
                  <img src={thumbUpWhite} />
                  <span>추천</span>
                </DetailLikeBtnFocus>
              ) : (
                <DetailLikeBtn
                  onClick={() => {
                    likeBtnHandler();
                  }}
                >
                  <img src={thumbUp} />
                  <span>추천</span>
                </DetailLikeBtn>
              )}
              <DetailEditBtnContainer>
                {data?.data.scrap === true ? (
                  <DetailUnscrapBtn
                    onClick={() => {
                      scrapBtnHandler();
                    }}
                  >
                    스크랩 취소
                  </DetailUnscrapBtn>
                ) : (
                  <DetailScrapBtn
                    onClick={() => {
                      scrapBtnHandler();
                    }}
                  >
                    스크랩
                  </DetailScrapBtn>
                )}
                {getEmail() === data.data.email ? (
                  <DetailEditBtn
                    onClick={() => {
                      patchBtnHandler();
                    }}
                  >
                    수정
                  </DetailEditBtn>
                ) : (
                  <></>
                )}
                {getEmail() === data.data.email ? (
                  <DetailCancelBtn
                    onClick={() => {
                      deleteBtnHandler();
                    }}
                  >
                    삭제
                  </DetailCancelBtn>
                ) : (
                  <></>
                )}
              </DetailEditBtnContainer>
            </DetailBtnContainer>
          </DetailMainContent>
        </DetailContentContainer>
        <Comment id={params.id} />
      </DetailBg>
    </>
  );
};

export default Detail;

const DetailBg = styled.div`
  position: relative;
  min-height: calc(100vh - 160px);
  padding: 0 20px 70px;
`;

const DetailContentContainer = styled.div`
  max-width: 1200px;
  margin: 40px auto 0;
  background-color: rgba(255, 255, 255, 0.7);
  box-shadow: 2px 4px 6px rgba(185, 185, 185, 0.25);
  border: 1px solid #eaeaea;
  border-radius: 12px;

  img {
    max-width: 100%;
  }
`;

const DetailArrowContainer = styled.button`
  width: 32px;
  height: 32px;

  img {
    width: 100%;
  }
`;

const DetailMainContent = styled.div`
  padding: 24px 40px;
`;

const DetailTitleContainer = styled.div`
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 16px;
  margin-bottom: 12px;
`;

const DetailTitle = styled.strong`
  display: inline-block;
  max-width: calc(100% - 40px);
  font-size: 24px;
  vertical-align: text-top;
  padding-left: 12px;
`;

const DetailSide = styled.div`
  display: flex;
  gap: 10px;
  margin-right: 12px;
  font-size: 14px;
`;

const DetailDiaryInfo = styled.span`
  font-weight: 400;
`;

const DetailReaderRecordContainer = styled.div`
  width: 100%;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
`;

const DetailReaderRecord = styled.div`
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;

  img {
    width: 16px;
    height: 16px;
  }
`;

const DetailConentParagraph = styled.div`
  line-height: 30px;
  padding: 30px;
  min-height: 560px;
`;

const DetailBtnContainer = styled.div`
  display: flex;
  justify-content: flex-end;
  align-items: center;
  border-top: 1px solid #eaeaea;
  padding-top: 12px;
`;

const DetailLikeBtn = styled.button`
  padding: 10px 20px;
  background-color: #ffffff;
  box-shadow: 0 0 0 1px #bebebe inset;
  border-radius: 4px;
  color: #444444;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;

  img {
    width: 16px;
    height: 16px;
  }
`;

const DetailLikeBtnFocus = styled(DetailLikeBtn)`
  background-color: #416dc9;
  color: #ffffff;
  box-shadow: none;
  animation: vibration 0.1s infinite;
  animation-iteration-count: 6;

  @keyframes vibration {
    from {
      transform: rotate(1deg);
    }
    to {
      transform: rotate(-1deg);
    }
  }
`;

const DetailEditBtnContainer = styled.div`
  width: 48%;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 7px;
`;

const DetailScrapBtn = styled(DetailLikeBtn)`
  background-color: #3f95ff;
  color: #ffffff;
  box-shadow: none;
`;
const DetailUnscrapBtn = styled(DetailLikeBtn)`
  background-color: #a1ccff;
  color: #ffffff;
  box-shadow: none;
`;
const DetailEditBtn = styled(DetailLikeBtn)`
  background-color: #ffc03f;
  color: #ffffff;
  box-shadow: none;
`;

const DetailCancelBtn = styled(DetailEditBtn)`
  background-color: #f36c68;
`;
