.PHONY: all report.tex

all: report.pdf

%.pdf: %.tex
	tectonic --reruns 0 $<
        # Run biber if we find a .bcf file in the output
	@if [ -f $(notdir $(<:.tex=.bcf)) ]; then \
	        echo "Running biber..."; \
		biber $(notdir $(<:.tex=)); \
	fi
	tectonic --reruns 1 $<
	rm $(basename $<).bbl $(basename $<).bcf $(basename $<).blg

count-%: %.tex
	texcount $<
