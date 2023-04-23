
SUMMARY = "A full-screen, console-based Python debugger"
HOMEPAGE = ""
AUTHOR = "Andreas Kloeckner <inform@tiker.net>"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e52c8eb1a3d2f453e3662bd43227effa"

SRC_URI = "https://files.pythonhosted.org/packages/85/a5/f1fd378f56bd8168b5921fd09d4b84fd8101a90e81402a509796caea2094/pudb-2022.1.3.tar.gz"
SRC_URI[md5sum] = "cda1c28dc52318162e2b131280f09960"
SRC_URI[sha256sum] = "58e83ada9e19ffe92c1fdc78ae5458ef91aeb892a5b8f0e7379e6fa61e0e664a"

S = "${WORKDIR}/pudb-2022.1.3"

RDEPENDS_${PN} = ""

inherit setuptools3
