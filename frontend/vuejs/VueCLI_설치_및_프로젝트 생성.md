# Vue CLI 설치 및 프로젝트 생성

npm 으로 OS 전역 범위에 vue-cli 를 설치해서 컴포넌트 기반의 프로젝트를 생성해보자~  

# vue cli 설치

```bash
$ npm install -g vue-cli
...

$ vue -V
...
또는
$ vue --version
```

## 참고) vue-cli 3.x 사용할때 2.x 명령어와 호환도록 할 경우

```bash
$ npm install @vue/cli-init -g
```

# vue CLI 로 컴포넌트 기반 프로젝트 생성

## 명령어 형식

```bash
$ vue init <template name> <project-name>
```

## ex)

```bash
$ vue init webpack helloworld
```

# 참고) Vue CLI 가 지원하는 템플릿 종류들

$ vue list 명령으로 확인 가능하다.

- webpack
- webpack-simple
- browserify
- browserify-simple
- paw-webpack
- simple

## ex)

```bash
$ vue list
```

