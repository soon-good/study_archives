# CRLF 와 LF로 인한 diff 시의 충돌문제 

Mac 윈도우를 사용하더라도 git diff 시 ~ ... only one difference line separator 이런 문구를 접할때가 있다. 이 경우 저장할 때는 CRLF로 저장되는 경우에 해당된다. 나의 경우는 최근에 ant로 빌드를 해야 할 일이 있었는데, ant로 빌드한 파일에서는 계속해서 다른 파일들 까지 수정되었다고 나타났었다.  

이 경우 해당 파일에 대해

- 저장소에서 가져올 때 LF를 CRLF로 변경하고
- 저장소로 보낼 때는 CRLF를 LF로 변경되도록 

수정해주면 된다.



## 참고자료

[https://www.lesstif.com/gitbook/git-crlf-20776404.html](https://www.lesstif.com/gitbook/git-crlf-20776404.html)



