# Ganada-Project

#### 2021-2 가천대학교 P-실무 프로젝트 "가나다"

## 다문화 가정 아이들을 위한 AI 기반 한글 교육 모바일 어플리케이션


### 프로젝트 소개
<br>
"가나다"는 다문화 가정 아이들이 한글을 보다 더 쉽게 학습할 수 있도록 도우기 위한 어플입니다. Image Classification 기술을 사용하여 실생활에서 사용하기 쉬운 어플을 기획했습니다. 또한, Imgae Captioning 기술을 사용해 이미지를 묘사하여 한글 학습에 도움을 주고자 했습니다.


---

### 구현 기능
<br>

![스토리보드3](https://user-images.githubusercontent.com/50989437/145930237-7d71a243-c756-4448-b67c-885503d15e8a.png)

1. 다국어 지원
2. 이미지에서 인식한 객체의 단어와 이미지를 묘사한 문장 제공 : 이미지 인식 + 이미지 캡셔닝
3. 이미지에서 인식한 글자와 해당 글자를 사용한 예문 제공
4. TTS : 단어와 예문을 음성으로 발화
5. 단어장 : 인식했던 단어를 저장

---

### 아키텍처
<br>

![image](https://user-images.githubusercontent.com/50989437/145930445-1c1bea96-132c-4239-960c-d793ef786675.png)

- 우리말 샘 API : 단어에 대한 예문 가져오기

- ML Kit : 이미지에서 글자 인식

- Microsoft Azure : 동적 이미지 캡셔닝

- Keras, TensorFlow : 이미지 분류, 정적 이미지 캡셔닝

- Android Studio : 프론트엔드 개발

- Flask : 백엔드 개발

---
### 인공지능 모델
<br>

* DataSet : Flickr8k
* Image Classification : MobileNetV2
* Static Image Captioning : ResNet101 + Attention
* Dynamic Image Captioning : Azure Vision API

---

### 참고 자료
<br>

1. O. Vinyals et al, Show and Tell: A Neural Image Caption Generator, In CVPR, 2015.
2. Kelvin Xu et al, Show, attend and tell: Neural image caption generation with visual attention, In ICML., 2015
3. Dzmitry Bahdanau et al, Neural machine translation by jointly learning to align and translate, CoRR, 2014
