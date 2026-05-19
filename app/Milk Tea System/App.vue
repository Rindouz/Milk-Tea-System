<script>
	import { useAuthStore } from '@/store/auth'

	export default {
		onLaunch: function() {
			console.log('App Launch')
			const authStore = useAuthStore()
			if (authStore.token) {
				authStore.isLogin = true
			}
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style lang="scss">
	@import '@/uni_modules/uni-scss/index.scss';
	@import '@/static/customicons.css';

	page {
		background-color: #f5f5f5;
	}

	.example-info {
		font-size: 14px;
		color: #333;
		padding: 10px;
	}
</style>
