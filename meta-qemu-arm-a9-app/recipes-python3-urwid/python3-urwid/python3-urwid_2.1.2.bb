
SUMMARY = "A full-featured console (xterm et al.) user interface library"
HOMEPAGE = "http://urwid.org/"
AUTHOR = "Ian Ward <ian@excess.org>"
LICENSE = "LGPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"

SRC_URI = "https://files.pythonhosted.org/packages/94/3f/e3010f4a11c08a5690540f7ebd0b0d251cc8a456895b7e49be201f73540c/urwid-2.1.2.tar.gz"
SRC_URI[md5sum] = "f7f4e6bed9ba38965dbd619520f39287"
SRC_URI[sha256sum] = "588bee9c1cb208d0906a9f73c613d2bd32c3ed3702012f51efe318a3f2127eae"

S = "${WORKDIR}/urwid-2.1.2"

RDEPENDS_${PN} = ""

inherit setuptools3
