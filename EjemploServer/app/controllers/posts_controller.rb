class PostsController < ApplicationController
  before_action :set_post, only: [:update, :destroy]
  respond_to :json
  skip_before_filter :verify_authenticity_token,
    if: Proc.new { |_user| _user.request.format == 'application/json' }
  # GET /posts
  # GET /posts.json
  def index
    p params
    @posts = Post.all
    respond_with @posts
  end

  # POST /posts
  # POST /posts.json
  def create
    @post = Post.new(post_params)
    # @post = Post.new(params[:post])
      if @post.save
        respond_with @post
      else
        respond_with @post.errors
    end
  end

  # PATCH/PUT /posts/1
  # PATCH/PUT /posts/1.json
  def update
      if @post.update(post_params)
        respond_with @post
      else
        respond_with @post.errors
      end
  end

  # DELETE /posts/1
  # DELETE /posts/1.json
  def destroy
    @post.destroy
    respond_with @post
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_post
      @post = Post.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def post_params
      params.require(:post).permit(:title, :content, :author, :rating)
    end
end
