module.exports = {
    chainWebpack: config => {
        config
            .plugin('html')
            .tap(args => {
                args[0].title = 'GameDeals'
                return args
            })
    },
    configureWebpack: {
        devServer: {
            watchOptions: {
                poll: true
            }
        }
    }
}