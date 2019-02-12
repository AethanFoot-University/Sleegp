# Report

This folder contains the final report for the project, typeset in LaTeX. The main document file is
`report.tex`, but as sections are added, they will be put in separate files inside `tex/`.

## Building

To build the report, the recommended method is using
[`tectonic`](https://tectonic-typesetting.github.io/en-US/). First, install `tectonic` using the
[instructions](https://tectonic-typesetting.github.io/en-US/install.html), then run:

```bash
$ tectonic report.tex
```

The report will be output to `report.pdf`.
