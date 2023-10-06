import { keyframes, styled } from "styled-components";
import { useState, useEffect } from "react";
import axios from "axios";
import SubWave from "../components/common/SubWave";
import Rain from "../components/Rain";
import Clear from "../components/Clear";
import Clouds from "../components/Clouds";
import Snow from "../components/Snow";

import MapGuide from "../assets/images/map_guide.png";
import AreaAll from "../assets/images/area_all.png";
import Area01 from "../assets/images/area_01.png";
import Area02 from "../assets/images/area_02.png";
import Area04 from "../assets/images/area_04.png";
import Area03 from "../assets/images/area_03.png";
import Area05 from "../assets/images/area_05.png";
import Area06 from "../assets/images/area_06.png";

import AreaAllAcive from "../assets/images/area_allActive.png";
import Area01Acive from "../assets/images/area_01Active.png";
import Area02Acive from "../assets/images/area_02Active.png";
import Area03Acive from "../assets/images/area_03Active.png";
import Area04Acive from "../assets/images/area_04Active.png";
import Area05Acive from "../assets/images/area_05Active.png";
import Area06Acive from "../assets/images/area_06Active.png";
import List from "../components/List";
import { useRecoilValue, useSetRecoilState } from "recoil";
import { areaState } from "../atoms/atoms";

const AreaMap = (): JSX.Element => {
  const [weather, setWeather] = useState("");
  const setArea = useSetRecoilState(areaState);
  const areaName = useRecoilValue(areaState);

  useEffect(() => {
    getAreaWeather();
  }, [areaName]);

  // 클릭한 지역을 활성화하고 지역 리스트를 불러오는 함수
  const handleAreaClick = (area: string) => {
    setArea(area);
    // 해당 지역에 대한 리스트를 불러오는 작업
  };

  function geoSuccess(position: GeolocationPosition): void {
    axios
      .get(
        `https://api.openweathermap.org/data/2.5/weather?lat=${position.coords.latitude}&lon=${position.coords.longitude}&appid=27306e0d72080c1e89bb4b2a519e6a55&units=metric`,
      )
      .then((res) => {
        setWeather(res.data.weather[0].main);
      });
  }

  function geoError(): void {
    return;
  }

  useEffect(() => {
    navigator.geolocation.getCurrentPosition(geoSuccess, geoError);
  }, []);

  function getAreaWeather(): void {
    if (areaName === null || areaName === "전체 지역") {
      navigator.geolocation.getCurrentPosition(geoSuccess, geoError);
    } else {
      let pos: number[] = [0, 0];

      if (areaName === "경기도") {
        pos = [37.567167, 127.190292];
      } else if (areaName === "강원도") {
        pos = [37.555837, 128.209315];
      } else if (areaName === "충청도") {
        pos = [36.628503, 127.929344];
      } else if (areaName === "경상도") {
        pos = [36.248647, 128.664734];
      } else if (areaName === "전라도") {
        pos = [35.716705, 127.144185];
      } else if (areaName === "제주도") {
        pos = [33.364805, 126.542671];
      }

      axios
        .get(
          `https://api.openweathermap.org/data/2.5/weather?lat=${pos[0]}&lon=${pos[1]}&appid=27306e0d72080c1e89bb4b2a519e6a55&units=metric`,
        )
        .then((res) => {
          setWeather(res.data.weather[0].main);
        });
    }
  }

  const renderWeatherComponent = () => {
    if (weather === null) {
      return null; // 날씨 정보가 없는 경우 아무것도 표시하지 않음
    }

    if (weather === "Rain") {
      return <Rain />;
    } else if (weather === "Clear") {
      return <Clear />;
    } else if (weather === "Clouds") {
      return <Clouds />;
    } else if (weather === "Snow") {
      return <Snow />;
    } else {
      return null;
    }
  };

  return (
    <MainCon>
      {areaName !== "전체 지역" && areaName !== null && renderWeatherComponent()}
      {areaName && <List></List>}

      <SubWave />
      <ConGuide>
        <AlArea onClick={() => handleAreaClick("전체 지역")}>
          {areaName !== "전체 지역" ? (
            <img src={AreaAll} alt="전체 지역" />
          ) : (
            <img src={AreaAllAcive} alt="전체 지역" />
          )}
        </AlArea>
        <AreaBox>
          <Area>
            {areaName !== "경기도" ? (
              <img src={Area01} alt="경기도" />
            ) : (
              <img src={Area01Acive} alt="경기도 지역 활성화" />
            )}
          </Area>
          <Area>
            {areaName !== "강원도" ? (
              <img src={Area02} alt="강원도" />
            ) : (
              <img src={Area02Acive} alt="강원도 지역 활성화" />
            )}
          </Area>
          <Area>
            {areaName !== "충청도" ? (
              <img src={Area03} alt="충청도" />
            ) : (
              <img src={Area03Acive} alt="충청도 지역 활성화" />
            )}
          </Area>
          <Area>
            {areaName !== "경상도" ? (
              <img src={Area04} alt="경상도" />
            ) : (
              <img src={Area04Acive} alt="경상도 지역 활성화" />
            )}
          </Area>
          <Area>
            {areaName !== "전라도" ? (
              <img src={Area05} alt="전라도" />
            ) : (
              <img src={Area05Acive} alt="전라도 지역 활성화" />
            )}
          </Area>
          <Area>
            {areaName !== "제주도" ? (
              <img src={Area06} alt="제주도" />
            ) : (
              <img src={Area06Acive} alt="제주도 지역 활성화" />
            )}
          </Area>

          <img src={MapGuide} alt="" useMap="#imagemap" className="mapImg" />
          <map name="imagemap">
            <area
              onClick={() => handleAreaClick("경기도")}
              shape="polygon"
              coords="87,93,104,97,142,64,182,43,234,95,239,124,269,160,253,207,261,220,242,234,220,237,198,259,173,251,134,255,123,234,111,230,114,220,101,208,101,196,117,191,116,182,95,173,97,164,105,153,102,137,105,127,87,99,83,89"
              alt=""
            />
            <area
              onClick={() => handleAreaClick("강원도")}
              shape="polygon"
              coords="189,41,198,32,327,39,347,27,362,2,370,6,370,14,389,45,388,62,421,132,447,158,472,244,382,248,294,212,266,218,259,208,274,160,241,120,241,93,184,38"
              alt=""
            />
            <area
              onClick={() => handleAreaClick("충청도")}
              shape="polygon"
              coords="55,246,65,236,110,247,126,260,172,256,194,263,224,245,242,240,267,222,295,217,378,253,361,275,362,284,322,293,302,308,280,300,276,320,277,353,283,364,299,373,297,378,281,384,263,410,236,405,228,403,210,412,190,413,169,394,154,394,146,408,134,411,124,406,113,413,113,426,101,431,104,418,70,382,76,343,71,324,76,313,73,304,54,298,42,297,36,284,39,248,52,247,59,266,54,245"
              alt=""
            />
            <area
              onClick={() => handleAreaClick("경상도")}
              shape="polygon"
              coords="383,253,470,250,485,307,466,420,474,426,494,401,494,431,474,510,432,551,428,562,402,576,359,566,332,581,326,610,321,615,315,601,305,595,314,585,312,573,300,590,285,590,265,601,249,590,241,561,230,540,224,516,235,497,235,458,264,435,267,417,287,387,307,383,306,369,283,353,281,322,286,311,309,314,330,297,369,289,368,280,383,253"
              alt=""
            />
            <area
              onClick={() => handleAreaClick("전라도")}
              shape="polygon"
              coords="91,444,121,429,120,416,128,413,140,417,150,413,159,400,169,400,177,412,197,419,220,418,236,409,259,414,262,425,230,452,226,495,217,515,237,570,233,573,223,597,219,599,220,619,228,629,225,634,202,615,205,592,188,596,189,610,198,622,193,636,183,644,138,645,145,631,176,613,170,605,156,608,126,635,106,636,65,670,53,665,57,630,42,640,40,670,0,661,28,631,29,619,19,599,26,591,39,605,28,582,45,570,37,560,24,561,19,554,39,548,58,561,28,512,39,507,52,490,59,479,87,475,60,464,57,452,92,443"
              alt=""
            />
            <area
              onClick={() => handleAreaClick("제주도")}
              shape="polygon"
              coords="31,739,34,727,76,706,135,697,161,710,161,733,148,746,105,762,68,757,57,764,33,762,24,746,31,738"
              alt=""
            />
          </map>
        </AreaBox>
      </ConGuide>
    </MainCon>
  );
};
export default AreaMap;

const MainCon = styled.main`
  position: relative;
  min-height: calc(100vh - 120px);
  display: flex;
  .mapImg {
    position: relative;
    z-index: 1;
    > area {
      cursor: pointer;
    }
  }
`;

const ConGuide = styled.div`
  position: relative;
  width: 100%;
  max-width: 1200px;
  min-height: 800px;
  margin: 0 auto;
  display: flex;
  align-items: center;
`;

const AreaBox = styled.div`
  position: absolute;
  left: calc(50% - 150px);
  width: 500px;
  height: 770px;
  z-index: 2;
  area {
    cursor: pointer;
  }
`;

const Bounce = keyframes`
  0%, 20%, 50%, 80%, 100% {transform: translateY(0);}
	40% {transform: translateY(-20px);}
	60% {transform: translateY(-15px);}
`;

const AlArea = styled.div`
  position: absolute;
  left: 0;
  top: calc(50% - 170px);
  animation: ${Bounce} 5s infinite;
  z-index: 2;
  cursor: pointer;
`;

const Area = styled.div`
  position: absolute;
  cursor: pointer;

  &:first-child {
    left: 87px;
    top: 42px;
  }
  &:nth-child(2) {
    right: 28px;
    top: 0;
  }
  &:nth-child(3) {
    left: 35px;
    top: 217px;
  }
  &:nth-child(4) {
    right: 2px;
    top: 249px;
  }
  &:nth-child(5) {
    left: 0px;
    top: 400px;
  }
  &:nth-child(6) {
    left: 26px;
    bottom: 0;
  }
`;
