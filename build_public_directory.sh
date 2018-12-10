#!/bin/bash
cd ../WrrkrrC && pwd
grunt build
cd ../WrrkrrS && pwd
echo "+ ATTEMPTING TO COPY ASSETS TO WRRKRR SERVER +"
rm -rf  public
cp -r ../WrrkrrC/dist .
mv dist public
echo "+ ASSETS READY +"