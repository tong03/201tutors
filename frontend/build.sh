rm -r ../dynamic-web-project/src/main/webapp/assets
rm -r ../dynamic-web-project/src/main/webapp/img
rm -r ../dynamic-web-project/src/main/webapp/index.html
npm run build
cp -r dist/* ../dynamic-web-project/src/main/webapp/