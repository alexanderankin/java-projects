# yaml2json

This tool needs a lot of work to be useful.
It can be modified to stream its data (at least for the core use case).

It also needs to support toml and csv.
And it also needs to support streaming to differentiate itself.
Otherwise, you can use some simple python scripts, such as `json-conv`.

## alternative: `json-conv`

if you need something like this, use [json-conv][json-conv]

[json-conv]: https://github.com/alexanderankin/side-projects/blob/main/misc/projects/json-converters/pyproject.toml

it can be found here: https://github.com/alexanderankin/side-projects/blob/main/misc/projects/json-converters/pyproject.toml

you can install it [like so](https://github.com/alexanderankin/dotfiles/blob/6c09249bbf3e1e473478fee369c8a8f017eeb018/.bash_aliases#L127C3-L127C486):

```shell
PKG=json-conv; BINS=(json2yaml yaml2json toml2json json2toml json2csv csv2json); { [[ -d ~/.$PKG-venv ]] || python -m venv ~/.$PKG-venv; } && { [[ -d ~/.$PKG-venv/bin ]] && . ~/.$PKG-venv/bin/activate || . ~/.$PKG-venv/Scripts/activate; } && pip install -U pip wheel && pip install 'git+https://github.com/alexanderankin/side-projects.git@main#egg='"$PKG"'&subdirectory=misc/projects/json-converters' && for b in "${BINS[@]}"; do ln -fvrs $(which $b) ~/.local/bin; done && deactivate
```
