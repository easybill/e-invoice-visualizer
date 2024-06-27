REPO_URL := https://github.com/itplr-kosit/xrechnung-visualization
TEMP_DIR := $(shell mktemp -d)
LOCAL_XSL_DIR := data/xsl
REPO_XSL_DIR := src/xsl

# main target
all: update-xsl clean

# sparse checkout
checkout:
	git clone --depth 1 --filter=blob:none --sparse $(REPO_URL) $(TEMP_DIR)
	cd $(TEMP_DIR) && git sparse-checkout set $(REPO_XSL_DIR)

# update XSL files
update-xsl: checkout
	rm -rf $(LOCAL_XSL_DIR)
	mv $(TEMP_DIR)/$(REPO_XSL_DIR) $(LOCAL_XSL_DIR)

# cleanup
clean:
	rm -rf $(TEMP_DIR)

.PHONY: all checkout update-xsl clean
