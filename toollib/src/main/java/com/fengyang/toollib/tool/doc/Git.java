package com.fengyang.toollib.utils.tool.doc;

/**
git 命令行：(Git Bash)
1.登录：ssh huihui@172.16.0.210 密码：wuhuihui123
2.修改密码：passwd
3.进入到自己得目录: cd /yangche/repository/huihui/
4.用ls 命令列出（list）自己目录下所有的文件目录
5.创建一个新的项目，如TestGit项目:  GIT_DIR=YourProject.git git init
6.在本地workspace创建一个同名的项目（TestGit)
7.进入到TestGit目录中，右键鼠标选择 Git Create repository here选项会创建一个.git文件夹
8.在workspace中提交：右键Git commit - > "master"-输入提交文件变化的说明，时间作者可选，选择要提交的有变化的文件，ok,push
      配置Manage：URL:uihui@172.16.0.210:/yangche/repository/huihui/YourProject.git,然后Add New/Save保存一下，点击确定
     点击Ok,并输入密码。
     每次提交的时候需要先commit然后在push,在push的时候一定要注意Local: 和 Remote： 要和第一次一样,所以建议两者每次都是master
9.cd ..返回上层目录
10.rm -rf YourProject.git删除一个Git管理的项目
   git pull huihui@172.16.0.210:/yangche/repository/huihui/TestGit.git 从服务器获取最新的内存
11.配置Eclipse中git:
   windows-Preferences-git(设置git clone的位置，建议是workspace路径)
   -Configuration(添加用户:邮箱wuhuihui@fengyangtech.com,名字huihui)
       在eclipse中项目右键team - Share project-右键项目： team-commit
       填写commit message，选择要上传的文件，如果项目中已经添加了.gitignore文件（作用是忽略/bin, /gen文件夹）
       点击Commit and Push则会把项目push到服务器
      配置路径：huihui@172.16.0.210:/yangche/repository/huihui/YourProject.git,输入密码
  refs/head/master-Add Spec-next-Finish
  Eclipse中直接Clone服务器Git项目
     空白处右键--Import-Git--Projects from Git

 */