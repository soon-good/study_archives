# aws python3,pip,virtualenv

aws amazon linux 2는 가장 안정적인 버전이지만 가장 큰 단점은 기본 설치된 python의 버전이 python2.7 이라는 것이다. 이제는 거의 종료된 버전인데 아직도 업데이트가 안되고 있고 yum update를 통해서도 업데이트가 안된다는 점이 참 이상하긴 하다.  

  

초기 설치시 python3,pip,virtualenv 설치과정을 정리한다.



# python3 설치

amazon linux 리포지터리에서 가장 최신인 python 3.6을 설치하려 했는데 지원되는 패키지가 3.5에 비해 얼마 없더라. 그리고 가장 지원되는 패키지가 많은 python3.4는 pip 설치시 

> $ python3 get-pip.py —user  
>
> RuntimeError: Python 3.5 or later is required

와 같은 에러를 내는 이유로 인해 python 3.5 버전을 설치했다. 2020/03/19 현재로는 그렇다. 추후 python3.5버전이 고인물 버전이 된다면 달라질 듯 하다.  

## python 3.5 설치

자, 이제 python 3.5 버전을 설치해보자.  

```bash
$ sudo yum list | grep python3
$ sudo yum install -y python35.x86_64
$ python3 get-pip.py —user
```



## python 명령어를 별칭으로 등록

python명령에 대해 python 2.7.x 버전이 동작하도록 되어 있는데 python 명령어를 별칭으로 등록해 python 명령실행시 python3.5 버전을 사용하도록 지정해주자.

```bash
$ vim ~/.bashrc
alias python="python3"
:wq
```



# pip 설치

python 3를 설치하고 가장 애를 먹었던 부분중 하나가 pip install 시 

> pip 20.0.2 from /usr/local/lib/python2.7/site-packages/pip (python 2.7)

와 같은 에러를 내는것이다. pip 사용시에는 또 python 2.7 버전을 사용한다. 그래서 python 2.7 을 지우려 하면 또 지우지 말라는 경고창이 떠서 조금 난감했다.  

이 경우에 대해 아래의 자료를 보고 해결했다.  

참고자료 : https://docs.aws.amazon.com/ko_kr/elasticbeanstalk/latest/dg/eb-cli3-install-linux.html

> python 대신 python3 명령을 사용하여 Python 버전 3을 직접 호출하면 이전 버전의 Python이 시스템에 있어도 pip가 적절한 위치에 설치됩니다.



자, 이제 pip 를 설치하고 설정해보자... 

## 패키지 매니저를 이용해 pip 설치

```bash
$ python3 get-pip.py —user
```

설치를 했지만

```bash
$ pip install virtualenv
Exception:
어쩌구 저쩌구…
….
….
  File "/usr/lib64/python2.7/shutil.py", line 326, in move
    os.unlink(src)
OSError: [Errno 13] 허가 거부: '/usr/bin/pip'
You are using pip version 9.0.3, however version 20.0.2 is available.
You should consider upgrading via the 'pip install --upgrade pip' command.
```

pip 가 기본으로 가리키고 있는 파이썬 버전이 2.7.x 여서 생기는 문제이다. 이 문제를 해결해보자.  



## 기본 pip 버전 변경 (pip 2 -> pip 3)

참고자료인 https://docs.aws.amazon.com/ko_kr/elasticbeanstalk/latest/dg/eb-cli3-install-linux.html 을 읽다가 힌트를 얻은 문구가 있다.

> 실행 경로 ~/.local/bin을 PATH 변수에 추가합니다.

이 말인 즉, aws에서는 ~/.local/bin에 우리가 ec2-user로 접속해 설치한 binary 녀석들이 저장된다는 이야기이다.  

```bash
$ ls -al ~/.local/bin/
합계 40
drwxrwxr-x 2 ec2-user ec2-user 4096  4월 19 06:56 .
drwx------ 4 ec2-user ec2-user 4096  4월 19 06:05 ..
-rwxrwxr-x 1 ec2-user ec2-user  230  4월 19 06:46 easy_install
-rwxrwxr-x 1 ec2-user ec2-user  230  4월 19 06:46 easy_install-3.5
-rwxrwxr-x 1 ec2-user ec2-user  221  4월 19 06:46 pip
-rwxrwxr-x 1 ec2-user ec2-user  221  4월 19 06:46 pip3
-rwxrwxr-x 1 ec2-user ec2-user  221  4월 19 06:46 pip3.5
-rwxrwxr-x 1 ec2-user ec2-user  221  4월 19 06:18 pip3.6
-rwxrwxr-x 1 ec2-user ec2-user  238  4월 19 06:56 virtualenv
-rwxrwxr-x 1 ec2-user ec2-user  208  4월 19 06:46 wheel
```

실제로 확인해보니 그러하다. 그래서 생각해낸 우회방법은 **ec2-user 사용자가 pip 라는 명령어를 alias로 등록하고, pip 명령어를 칠때마다 pip3 를 실행회도록** 하는 것이다.

```bash
$vim ~/.bashrc
alias python="python3"
alias pip="pip3"

:wq
```

위의 내용을 입력했으면, 설정 파일을 적용하기 위해

> $ source ~/.bashrc

를 해주자.  

버전을 확인해보자.  

```bash
$ pip --version
pip 20.0.2 from /home/ec2-user/.local/lib/python3.5/site-packages/pip (python 3.5)
```

제대로 지정된다.



# virtualenv 설치

virtualenv는 python에서 가장 대중적으로 사용되는 가상환경이다. 설치를 진행해보자.

```bash
$ pip install virtualenv
```

끝~  