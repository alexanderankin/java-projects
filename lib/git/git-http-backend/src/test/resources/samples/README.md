# samples

this folder contains sample git repositories.

Since a git repository cannot contain nested repositories,
as git always treats `.git` folders in a special way,
they are zipped before being committed here, and excluded via `.gitignore`.

```shell
zip -r sample-git-repository.zip sample-git-repository
```
