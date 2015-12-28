# Git
## Git - Undo git add
``` 
git rm -r --cached {path}
``` 
or 
``` 
git reset HEAD {path}
``` 

## Git add empty project to remote repository
git init
git remote add origin https://github.com/user/repository.git
git push -u origin master

## git pull 和 git fetch 区别
``` 
git pull = git fetch + merge to local
``` 

https://git-scm.com/book/en/v2/Git-Branching-Basic-Branching-and-Merging


当不带任何参数执行git push命令时，实际执行过程是:
1. 如果为当前分支设置了<remote>，即由配置branch.<branchname>.remote给出了远程版本库代号，则不带参数执行git push相当于执行了git push <remote>。
2. 如果没有为当前分支设置<remote>，则不带参数执行git push相当于执行了git push origin。
3. 要推送的远程版本库的URL由remote.<remote>.pushurl给出。如果没有配置，则使用remote.<remote>.url配置的URL地址。
4. 如果为注册的远程版本库设置了push参数，即通过remote.<remote>.push配置了一个引用表达式，则使用该引用表达式执行推送。
5. 否则使用“:”作为引用表达式。该表达式的含义是同名分支推送，即对所有在远程版本库中有同名分支的本地分支执行推送。
当不带任何参数执行git pull命令时，实际执行过程是：
1. 如果为当前branch设置了<remote>，即由配置branch.<branchname>.remote出了远程版本库代号，则不带参数执行git pull相当于执行了git pull <remote>。
2. 如果没有为当前分支设置<remote>，则不带参数执行git pull相当于执行了git pull origin 
3. 要获取的远程版本库的URL地址由remote.<remote>.url给出。
4. 如果为注册的远程版本库设置了fetch参数，即通过remote.<remote>.fetch配置了一个引用表达式,则使用该引用表达式执行获取操作
5. 接下来要确定合并分支。如果设定了branch.<branchname>.merge，则对其设定的分支执行合并，否则报错退出。
 下面说说自己的理解：
1. 首先说说remote.<remote>.push和remote.<remote>.fetch这两个配置项。remote.<remote>.push是没有缺省值的，而remote.<remote>.fetch有缺省值为"+refs/heads/*:refs/remotes/<remote>/*"。因此当不带参数执行git pull时，将会强制将远程所有branch强制更新到本地refs/remotes/<remote>/下。
2. 当带参数执行git pull时，例如git pull origin test。由于指定的remote以及branch名。因此将直接update本地test分支，但不会更新remotes/origin/test分支。因此当git pull origin test后，再次checkout到test分支时，会出现类似“Your branch is ahead of the tracked remote branch 'origin/test' by 1 commit. ”的提示信息。
3. 前面说到remote.<remote>.push没有缺省值，这时会使用“:”作为引用表达式。即对所有在远程版本库中有同名分支的本地分支执行推送。git push的这个缺省行为可以通过配置选项push.default设置。详细参数可以参考git help config。